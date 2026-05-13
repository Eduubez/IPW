package pt.isel.ipw.services

import org.springframework.stereotype.Service
import pt.isel.ipw.domain.ActivityActions
import pt.isel.ipw.domain.mapToString
import pt.isel.ipw.domain.process.ProcessView
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.repository.Transaction
import pt.isel.ipw.repository.TransactionManager
import pt.isel.ipw.services.errors.*
import pt.isel.ipw.services.interfaces.ProcessService
import pt.isel.ipw.services.results.AssignInvestigatorResult
import pt.isel.ipw.services.results.AssignSupervisorResult
import pt.isel.ipw.services.results.CancelProcessResult
import pt.isel.ipw.services.results.ChangeEndDateResult
import pt.isel.ipw.services.results.ChangePriorityResult
import pt.isel.ipw.services.results.CreateProcessResult
import pt.isel.ipw.services.results.GetAllProcessesResult
import pt.isel.ipw.services.results.GetProcessResult
import pt.isel.ipw.services.results.ProcessValidationResult
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

            if (note != null) { /*noteServices.createNote(note)*/
            }

            // INSURANCE AND TYPIFICATIONS

            // Deveria estar a utilizar o NoteServices para escrever as notas

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
                ActivityActions.CREATED.mapToString(),
                "Process created by ${triator.name}"
            )

            success(processId)

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
        offset: Int?,
        limit: Int?,
        userId: Int,
        role: String
    ): GetAllProcessesResult =
        transactionManager.run {
            // so pode ver os processos associados a si - Investigador
            // so pode ver os processos da sua area - Supervisor
            // ao passar apenas o userId e não a area, como o processos possui sempre quem é o Investigador e Supervisor, basta filtrar pelos seus ids

            val validation = validateFilters(limit, offset)

            val resolvedId = resolvedUserId(userId, role)

            if (validation is Failure) {
                return@run failure(validation.value)
            }

            val processes = processRepository.getAll(offset ?: 0, limit ?: 10, resolvedId)
            return@run success(processes)

        }

    override fun changeEndDate(processId: Int, endDate: String, userId: Int, role: String): ChangeEndDateResult =
        transactionManager.run {
            val user = usersRepository.getUserById(userId) ?: return@run failure(ProcessError.InvalidUserId)
            val process = processRepository.getById(processId) ?: return@run failure(ProcessError.ProcessNotFound)

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


    // mudar process state para assign-to-Ivs
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

    // mudar process state para assign-to-Sup
    override fun assignSupervisor(processId: Int, triatorId: Int, supervisorId: Int): AssignSupervisorResult =
        transactionManager.run {
            // mudar o state do process
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


    override fun changePriority(processId: Int, newPriority: String, userId: Int): ChangePriorityResult =
        transactionManager.run {
            val user = usersRepository.getUserById(userId) ?: return@run failure(ProcessError.InvalidUserId)
            val process = processRepository.getById(processId) ?: return@run failure(ProcessError.ProcessNotFound)

            if (userId != process.supervisor?.id) return@run failure(ProcessError.InvalidSupervisor)
            if (!validatePriority(newPriority)) return@run failure(ProcessError.InvalidPriority)

            processRepository.updateProcessPriority(processId, newPriority.lowercase())

            activityServices.createActivity(
                processId,
                userId,
                ActivityActions.CHANGED_PRIORITY.mapToString(),
                "Priority changed to $newPriority by ${user.name}"
            )

            return@run success(Unit)
        }

    //apenas o manager
    override fun cancelProcess(processId: Int, userId: Int): CancelProcessResult =
        transactionManager.run {

            if (!validateProcess(processId)) return@run failure(ProcessError.ProcessNotFound)
            processRepository.cancelProcess(processId)

            activityServices.createActivity(
                processId,
                userId,
                ActivityActions.CANCELLED.mapToString(),
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
        priority.lowercase() in listOf("normal", "with_priority", "urgent")


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
            "manager" -> null
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

    private fun validateFilters(limit: Int?, offset: Int?): ProcessValidationResult {
        if (limit != null && limit <= 0) return failure(ProcessError.InvalidLimit)
        if (offset != null && offset < 0) return failure(ProcessError.InvalidOffset)
        return success(Unit)
    }

    private fun Transaction.validateProcess(processId: Int): Boolean {
        processRepository.getById(processId) ?: return false
        return true

    }


    private fun validateProcessRelation(
        process: ProcessView,
        userId: Int,
        role: String
    ): ProcessValidationResult {
        if (process.investigator?.id == userId || process.supervisor?.id == userId || role == "manager") {
            return success(Unit)
        }
        return failure(ProcessError.UnauthorizedAccess)

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


}
