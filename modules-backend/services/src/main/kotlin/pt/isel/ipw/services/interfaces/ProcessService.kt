package pt.isel.ipw.services.interfaces

import pt.isel.ipw.services.results.AssignInvestigatorResult
import pt.isel.ipw.services.results.AssignSupervisorResult
import pt.isel.ipw.services.results.CancelProcessResult
import pt.isel.ipw.services.results.ChangeEndDateResult
import pt.isel.ipw.services.results.ChangePriorityResult
import pt.isel.ipw.services.results.CreateProcessResult
import pt.isel.ipw.services.results.GetAllProcessesResult
import pt.isel.ipw.services.results.GetProcessResult

interface ProcessService {


    fun createProcess(
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
    ): CreateProcessResult


    fun getProcessById(id: Int, userId: Int, role: String): GetProcessResult

    fun getAllProcesses(offset: Int?, limit: Int?, userId: Int, role: String): GetAllProcessesResult

    fun changeEndDate(processId: Int, endDate: String, userId: Int, role: String): ChangeEndDateResult

    fun assignInvestigator(processId: Int, triatorId: Int, investigatorId: Int): AssignInvestigatorResult

    fun assignSupervisor(processId: Int, triatorId: Int, supervisorId: Int): AssignSupervisorResult

    fun changePriority(processId: Int, newPriority: String, userId: Int): ChangePriorityResult

    fun cancelProcess(processId: Int, userId: Int): CancelProcessResult

}
