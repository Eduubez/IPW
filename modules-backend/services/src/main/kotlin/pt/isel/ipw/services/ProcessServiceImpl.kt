package pt.isel.ipw.services

import org.springframework.stereotype.Service
import pt.isel.ipw.domain.ActivityActions
import pt.isel.ipw.domain.ListProps
import pt.isel.ipw.domain.mapToString
import pt.isel.ipw.domain.process.Priority
import pt.isel.ipw.domain.process.ProcessView
import pt.isel.ipw.domain.process.AssignmentStateRole
import pt.isel.ipw.domain.process.State
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.repository.Transaction
import pt.isel.ipw.repository.TransactionManager
import pt.isel.ipw.services.errors.*
import pt.isel.ipw.services.interfaces.AreaService
import pt.isel.ipw.services.interfaces.ProcessService
import pt.isel.ipw.services.results.*
import java.time.LocalDateTime

@Service
class ProcessServiceImpl(
    private val transactionManager: TransactionManager,
    private val activityServices: ActivityServiceImpl,
    ) : ProcessService {


    override fun createProcess(
        userId: Int,
        name: String,
        street: String,
        county: String,
        district: String,
        latitude: Int?,
        longitude: Int?,
        area: String,
        priority: String,
        expiresAt: String,
        investigatorId: Int?,
        supervisorId: Int?,
        insuranceId: Int?,
        typificationId: Int?,
        canBeFraud: Boolean,
        note: String?,
    ): CreateProcessResult {
        return transactionManager.run {

            val triator = usersRepository.getUserById(userId) ?: return@run failure(ProcessError.InvalidTriator)

            val validation = validateProcessFields(
                name,
                street,
                county,
                district,
                latitude,
                longitude,
                area,
                priority,
                expiresAt,
                investigatorId,
                supervisorId,
                insuranceId,
                typificationId
            )

            if (validation is Failure) {
                return@run failure(validation.value)
            }

            val processId = processRepository.createProcess(
                userId,
                name,
                street,
                county,
                district,
                latitude,
                longitude,
                area,
                priority,
                expiresAt,
                investigatorId,
                supervisorId,
                1,          //insuranceId,
                1,          //typificationId,
                canBeFraud,
                note
            )

            activityServices.createActivity(
                processId,
                userId,
                ActivityActions.CREATED_PROCESS.mapToString(),
                "Process created by ${triator.name}"
            )

            success(processId)

        }
    }

    override fun submitProcess(
        userId: Int,
        role: String,
        processId: Int
    ): SubmitProcessResult {
        return transactionManager.run {
            val user = usersRepository.getUserById(userId) ?: return@run failure(ProcessError.InvalidUserId)

            val process = processRepository.getById(processId) ?: return@run failure(ProcessError.ProcessNotFound)
            val validation = validateProcessRelation(process, userId, role)

            if (process.report == null) return@run failure(ProcessError.ReportNotFound)
            if (validation is Failure) {
                return@run failure(validation.value)
            }

            val allowedStates = AssignmentStateRole.getStates(role)
            if (process.state !in allowedStates) {
                return@run failure(ProcessError.InvalidState)
            }

            processRepository.changeState(processId, State.WAITING_APPROVAL_SUPERVISOR.toString())

            activityServices.createActivity(
                processId,
                userId,
                ActivityActions.SUBMIT_PROCESS_FOR_APPROVAL.mapToString(),
                "Process submitted by ${formatUserName(user.name, role)}"
            )

            success(Unit)
        }
    }

    override fun getProcessById(id: Int, userId: Int, role: String): GetProcessResult =
        transactionManager.run {
            val process = processRepository.getById(id) ?: return@run failure(ProcessError.ProcessNotFound)
            val validation = validateProcessRelation(process, userId, role)

            if (validation is Failure) {
                return@run failure(validation.value)
            }

            success(process)
        }


    override fun getAllProcesses(
        offset: Int,
        limit: Int,
        history: Boolean?,
        name: String,
        priority: String,
        state: String,
        userId: Int,
        role: String
    ): GetAllProcessesResult =
        transactionManager.run {

            val validation = validateFilters(limit, offset, priority, state)

            val areaValidation = checkAreaId(userId, role)

            if (validation is Failure) {
                return@run failure(validation.value)
            }

            if (areaValidation is Failure) {
                return@run failure(areaValidation.value)
            }

            val areaId = (areaValidation as Success).value

            val targetStates = resolveTargetStates(role, history)

            val (processes, totalCount) =processRepository.getAll(
                offset,
                limit,
                name,
                priority,
                state,
                areaId ?: 0,
                userId,
                role,
                targetStates
            )

            val hasNext = offset + processes.size < totalCount

            return@run success(Pair(processes, ListProps(hasNext, totalCount)))

        }

    override fun changeEndDate(processId: Int, endDate: String, userId: Int, role: String): ChangeEndDateResult =
        transactionManager.run {
            val user = usersRepository.getUserById(userId) ?: return@run failure(ProcessError.InvalidUserId)
            val process = processRepository.getById(processId) ?: return@run failure(ProcessError.ProcessNotFound)
            if (process.state in setOf(
                    State.CANCELED,
                    State.APPROVED_BY_MANAGER
                )
            ) return@run failure(ProcessError.ProcessFinished)

            if (!isAuthorizedToChangeEndDate(process, userId, role)) return@run failure(ProcessError.InvalidSupervisor)
            if (!validateExpireDate(endDate)) return@run failure(ProcessError.InvalidExpirationDate)

            processRepository.updateEndDate(processId, endDate)

            activityServices.createActivity(
                processId,
                userId,
                ActivityActions.CHANGED_END_DATE.mapToString(),
                "End date changed to $endDate by ${formatUserName(user.name, role)}"
            )

            success(Unit)
        }


    override fun assignInvestigator(processId: Int, triatorId: Int, investigatorId: Int): AssignInvestigatorResult =
        transactionManager.run {
            val triator =
                usersRepository.getUserById(triatorId) ?: return@run failure(ProcessError.InvalidTriator)
            val investigator =
                usersRepository.getUserById(investigatorId) ?: return@run failure(ProcessError.InvalidInvestigator)
            val process = processRepository.getById(processId) ?: return@run failure(ProcessError.ProcessNotFound)

            if (process.triator.id != triatorId) return@run failure(ProcessError.InvalidTriator)
            if (process.area.name != investigator.area) return@run failure(ProcessError.InvalidInvestigator)


            processRepository.updateProcessInvestigator(processId, investigatorId)

            activityServices.createActivity(
                processId,
                triatorId,
                ActivityActions.ASSIGNED_SUPERVISOR.mapToString(),
                "Investigator ${investigator.name} assigned by ${triator.name}"
            )

            return@run success(Unit)

        }

    override fun assignSupervisor(processId: Int, triatorId: Int, supervisorId: Int): AssignSupervisorResult =
        transactionManager.run {
            val triator = usersRepository.getUserById(triatorId) ?: return@run failure(ProcessError.InvalidTriator)
            val supervisor =
                usersRepository.getUserById(supervisorId) ?: return@run failure(ProcessError.InvalidSupervisor)
            val process = processRepository.getById(processId) ?: return@run failure(ProcessError.ProcessNotFound)

            if (process.triator.id != triatorId) return@run failure(ProcessError.InvalidTriator)
            if (process.area.name != supervisor.area) return@run failure(ProcessError.InvalidSupervisor)
            processRepository.updateProcessSupervisor(processId, supervisorId)

            activityServices.createActivity(
                processId,
                triatorId,
                ActivityActions.ASSIGNED_SUPERVISOR.mapToString(),
                "Supervisor ${supervisor.name} assigned by ${triator.name}"
            )

            return@run success(Unit)

        }


    override fun changePriority(processId: Int, newPriority: String, userId: Int, role: String): ChangePriorityResult =
        transactionManager.run {
            val user = usersRepository.getUserById(userId) ?: return@run failure(ProcessError.InvalidUserId)
            val process = processRepository.getById(processId) ?: return@run failure(ProcessError.ProcessNotFound)
            if (process.state in setOf(
                    State.CANCELED,
                    State.APPROVED_BY_MANAGER
                )
            )  return@run failure(ProcessError.ProcessFinished)

            val validation = validateProcessRelation(process, userId, role)

            if (validation is Failure) {
                return@run failure(ProcessError.InvalidSupervisor)
            }

            if (!validatePriority(newPriority)) return@run failure(ProcessError.InvalidPriority)

            processRepository.updateProcessPriority(processId, newPriority.lowercase())

            activityServices.createActivity(
                processId,
                userId,
                ActivityActions.CHANGED_PRIORITY.mapToString(),
                "Priority changed to $newPriority by ${formatUserName(user.name, role)}"
            )

            return@run success(Unit)
        }

    override fun updatePrioritiesByDeadline(): UpdateProcessPrioritiesResult =
        transactionManager.run {
            val updatedProcesses = processRepository.updatePrioritiesByDeadline()
            success(updatedProcesses)
        }

    override fun cancelProcess(processId: Int, userId: Int): CancelProcessResult =
        transactionManager.run {

            val process = processRepository.getById(processId) ?: return@run failure(ProcessError.ProcessNotFound)
            if (process.state in setOf(
                    State.CANCELED,
                    State.APPROVED_BY_MANAGER
                )
            )  return@run failure(ProcessError.ProcessFinished)

            processRepository.cancelProcess(processId)

            activityServices.createActivity(
                processId,
                userId,
                ActivityActions.CANCELLED_PROCESS.mapToString(),
                "Process cancelled by manager"
            )

            return@run success(Unit)
        }


    private fun validateExpireDate(date: String): Boolean {
        return try {
            val parsedDate = LocalDateTime.parse(date)
            parsedDate.isAfter(LocalDateTime.now())
        } catch (e: Exception) {
            false
        }
    }


    private fun validateLocation(
        street: String,
        county: String,
        district: String,
        latitude: Int?,
        longitude: Int?
    ): Boolean =
        street.isNotBlank() && county.isNotBlank() && district.isNotBlank()

    private fun validateName(processName: String): Boolean =
        processName.length > 3


    private fun validatePriority(priority: String): Boolean =
        Priority.mapStringToPriority(priority) != null


    private fun Transaction.validateInvestigator(investigatorId: Int?, area: String): Boolean {
        if (investigatorId == null) return true

        val user =
            usersRepository.getUserById(investigatorId) ?: return false

        return user.area == area
    }

    private fun isAuthorizedToChangeEndDate(process: ProcessView, userId: Int, role: String): Boolean =
        role == Roles.MANAGER || userId == process.supervisor?.id

    private fun formatUserName(name: String, role: String): String =
        if (role == Roles.MANAGER) "Manager - $name" else name

    private fun resolvedUserId(userId: Int, role: String): Int? =
        when (role) {
            Roles.MANAGER -> null
            else -> userId
        }


    private fun validateTypification(typificationId: Int?): Boolean {
        return true
    }

    private fun validateInsurance(insuranceId: Int?): Boolean {
        return true
    }


    private fun Transaction.validateSupervisor(supervisorId: Int?, area: String): Boolean {
        if (supervisorId == null) return true
        val user = usersRepository.getUserById(supervisorId) ?: return false
        return user.area == area
    }

    private fun validateFilters(
        limit: Int?,
        offset: Int?,
        priority: String,
        state: String
    ): ProcessValidationResult {
        if (limit != null && limit <= 0) return failure(ProcessError.InvalidLimit)
        if (offset != null && offset < 0) return failure(ProcessError.InvalidOffset)
        if (state.isNotBlank()) {
            State.mapStringToState(state) ?: return failure(ProcessError.InvalidState)
        }
        if(priority.isNotBlank()) {
            Priority.mapStringToPriority(priority) ?: return failure(ProcessError.InvalidPriority)
        }
        return success(Unit)
    }



    private fun validateProcessRelation(
        process: ProcessView,
        userId: Int,
        role: String
    ): ProcessValidationResult {
        val authorized = when (role) {
            Roles.MANAGER -> true
            Roles.INVESTIGATOR -> process.investigator?.id == userId
            Roles.SUPERVISOR -> process.supervisor?.id == userId
            Roles.TRIATOR -> process.triator.id == userId
            else -> false
        }

        return if (authorized) success(Unit) else failure(ProcessError.UnauthorizedAccess)
    }


    private fun Transaction.validateProcessFields(
        name: String,
        street: String,
        county: String,
        district: String,
        latitude: Int?,
        longitude: Int?,
        area: String,
        priority: String,
        expiresAt: String,
        investigatorId: Int?,
        supervisorId: Int?,
        insuranceId: Int?,
        typificationId: Int?,
    ): ProcessValidationResult {

        if (!validateName(name)) return failure(ProcessError.InvalidName)
        if (!validateLocation(
                street,
                county,
                district,
                latitude,
                longitude
            )
        ) return failure(ProcessError.InvalidLocation)
        if (!validateExpireDate(expiresAt)) return failure(ProcessError.InvalidExpirationDate)
        if (!validatePriority(priority)) return failure(ProcessError.InvalidPriority)
        if (!validateInvestigator(investigatorId, area)) return failure(ProcessError.InvalidInvestigator)
        if (!validateSupervisor(supervisorId, area)) return failure(ProcessError.InvalidSupervisor)
        if (!validateInsurance(insuranceId)) return failure(ProcessError.InvalidInsurance)
        if (!validateTypification(typificationId)) return failure(ProcessError.InvalidTypification)

        return success(Unit)
    }

    private fun Transaction.checkAreaId(userId: Int, role: String): CheckAreaIdResult {
        if (role != Roles.SUPERVISOR) return success(null)
        val area = areasRepository.getAreaByUserId(userId) ?: return failure(ProcessError.InvalidAreaId)
        return success(area.areaId)
    }


    private fun resolveTargetStates(role: String, history: Boolean?): List<String> =

        if (history == null || !history) {
            AssignmentStateRole.getStates(role).map { it.toString() }
        } else {
            AssignmentStateRole.getHistoryStates().map { it.toString() }
        }


}




