package pt.isel.ipw.services

import org.springframework.stereotype.Service
import pt.isel.ipw.domain.ActivityActions
import pt.isel.ipw.domain.mapToString
import pt.isel.ipw.domain.process.ProcessView
import pt.isel.ipw.domain.process.State
import pt.isel.ipw.domain.report.Report
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.repository.TransactionManager
import pt.isel.ipw.services.errors.*
import pt.isel.ipw.services.interfaces.ActivityService
import pt.isel.ipw.services.interfaces.ReportService


typealias CreateReportResult = Either<ReportError, Int>


@Service
class ReportServiceImpl(
    private val transactionManager: TransactionManager,
    private val activityService: ActivityService,
) : ReportService {


    override fun createReport(processId: Int, content: String, userId: Int): CreateReportResult =
        transactionManager.run {

            if (!validateContent(content)) return@run failure(ReportError.InvalidContent)
            val process = processRepository.getById(processId) ?: return@run failure(ReportError.ProcessNotFound)


            if (!isInvestigator(process, userId)) return@run failure(ReportError.Unauthorized)

            val reportId = reportRepository.createReport(processId, content)

            processRepository.changeState(processId, "waiting_approval_supervisor")

            activityService.createActivity(
                processId,
                process.investigator!!.id,
                ActivityActions.CREATED_REPORT.mapToString(),
                "Report created by ${process.investigator!!.name}"
            )

            return@run success(reportId)
        }


    override fun getByProcessId(processId: Int, userId: Int, role: String): Either<ReportError, Report> =
        transactionManager.run {

            val process = processRepository.getById(processId) ?: return@run failure(ReportError.ProcessNotFound)
            if (!validateProcessRelation(process, userId, role)) return@run failure(ReportError.NotAssociated)

            val report = reportRepository.getByProcessId(processId)
                ?: return@run failure(ReportError.ReportNotFound)

            return@run success(report)
        }

    override fun updateReport(
        processId: Int,
        content: String,
        userId: Int
    ): Either<ReportError, Unit> = transactionManager.run {

        if (!validateContent(content)) return@run failure(ReportError.InvalidContent)

        val process = processRepository.getById(processId)
            ?: return@run failure(ReportError.ProcessNotFound)

        val reportId = process.report?.id ?: return@run failure(ReportError.ReportNotFound)

        if (!isInvestigator(process, userId)) return@run failure(ReportError.UnauthorizedInvestigator)

        reportRepository.updateReport(reportId, content)

        activityService.createActivity(
            processId,
            userId,
            ActivityActions.UPDATED_REPORT.mapToString(),
            "Report updated by ${process.investigator!!.name}"
        )

        return@run success(Unit)
    }

    override fun deleteReport(processId: Int, userId: Int): Either<ReportError, Unit> =
        transactionManager.run {

            val process = processRepository.getById(processId)
                ?: return@run failure(ReportError.ProcessNotFound)

            val reportId = process.report?.id ?: return@run failure(ReportError.ReportNotFound)

            if (!isInvestigator(process, userId)) return@run failure(ReportError.Unauthorized)

            reportRepository.deleteReport(reportId)

            activityService.createActivity(
                userId,
                userId,
                ActivityActions.DELETED_REPORT.mapToString(),
                "Report deleted by ${process.investigator!!.name}"
            )

            return@run success(Unit)
        }

    override fun approveReport(processId: Int, userId: Int, role: String): Either<ReportError, Unit> =
        transactionManager.run {

            val process = processRepository.getById(processId)
                ?: return@run failure(ReportError.ProcessNotFound)

            reportRepository.getByProcessId(processId)
                ?: return@run failure(ReportError.ReportNotFound)

            val verification = verifyApproval(process, userId, role)

            if(verification is Failure){
                return@run verification
            }

            val (newState, activityAction) = (verification as Success).value

            processRepository.changeState(processId, newState)

            if(role == Roles.SUPERVISOR)
                processRepository.changeState(processId, State.WAITING_APPROVAL_MANAGER.toString())

            activityService.createActivity(
                processId,
                userId,
                activityAction.mapToString(),
                "Report approved by ${if (isSupervisor(process, userId)) process.supervisor!!.name else Roles.MANAGER}"
            )

            return@run success(Unit)
        }

    override fun rejectReport( processId: Int, userId: Int, role: String): Either<ReportError, Unit> =
        transactionManager.run {

            val process = processRepository.getById(processId)
                ?: return@run failure(ReportError.ProcessNotFound)

            reportRepository.getByProcessId(processId)
                ?: return@run failure(ReportError.ReportNotFound)

            val verification = verifyRejection(process, userId, role)

            if (verification is Failure) return@run verification

            val (newState, activityAction) = (verification as Success).value

            processRepository.changeState(processId, newState)

            activityService.createActivity(
                processId,
                userId,
                activityAction.mapToString(),
                "Report rejected by ${if (isSupervisor(process, userId)) process.supervisor!!.name else Roles.MANAGER}"
            )

            return@run success(Unit)
        }


    private fun validateContent(content: String): Boolean =
        content.isNotBlank()


    private fun isInvestigator(process: ProcessView, userId: Int): Boolean =
        process.investigator?.id == userId

    private fun isSupervisor(process: ProcessView, userId: Int): Boolean =
        process.supervisor?.id == userId

    private fun isManager(role: String): Boolean =
        role == Roles.MANAGER


    private fun validateProcessRelation(
        process: ProcessView,
        userId: Int,
        role: String
    ): Boolean {
        return process.investigator?.id == userId || process.supervisor?.id == userId || role == Roles.MANAGER

    }


    private fun verifyApproval(process: ProcessView, userId: Int, role: String): Either<ReportError, Pair<String, ActivityActions>> =
        when {
            isSupervisor(process, userId) -> {
                when (process.state) {
                    State.WAITING_APPROVAL_SUPERVISOR ->
                        success(State.APPROVED_BY_SUPERVISOR.toString() to ActivityActions.APPROVED_REPORT_SUPERVISOR)
                    State.APPROVED_BY_SUPERVISOR ->
                        failure(ReportError.AlreadyApproved)
                    State.WAITING_APPROVAL_MANAGER ->
                        failure(ReportError.AlreadyApproved)
                    else ->
                        failure(ReportError.InvalidState)
                }
            }
            isManager(role) -> {
                when (process.state) {
                    State.WAITING_APPROVAL_MANAGER ->
                        success(State.APPROVED_BY_MANAGER.toString() to ActivityActions.APPROVED_REPORT_MANAGER)
                    State.WAITING_APPROVAL_SUPERVISOR ->
                        failure(ReportError.NotApprovedBySupervisor)
                    State.APPROVED_BY_MANAGER ->
                        failure(ReportError.AlreadyApproved)
                    else ->
                        failure(ReportError.InvalidState)
                }
            }
            else -> failure(ReportError.Unauthorized)
        }


    private fun verifyRejection(process: ProcessView, userId: Int, role: String): Either<ReportError, Pair<String, ActivityActions>> =
        when {
            isSupervisor(process, userId) -> {
                when (process.state) {
                    State.WAITING_APPROVAL_SUPERVISOR ->
                        success(State.REJECTED_BY_SUPERVISOR.toString() to ActivityActions.REJECTED_REPORT_SUPERVISOR)
                    State.REJECTED_BY_SUPERVISOR ->
                        failure(ReportError.AlreadyRejected)

                    State.WAITING_APPROVAL_MANAGER ->
                        failure(ReportError.AlreadyApproved)
                    else ->
                        failure(ReportError.InvalidState)
                }
            }
            isManager(role) -> {
                when (process.state) {
                    State.WAITING_APPROVAL_MANAGER ->
                        success(State.REJECTED_BY_MANAGER.toString() to ActivityActions.REJECTED_REPORT_MANAGER)
                    State.WAITING_APPROVAL_SUPERVISOR ->
                        failure(ReportError.NotApprovedBySupervisor)
                    State.REJECTED_BY_MANAGER ->
                        failure(ReportError.AlreadyRejected)
                    else ->
                        failure(ReportError.InvalidState)
                }
            }
            else -> failure(ReportError.Unauthorized)
        }



}