package pt.isel.ipw.services.interfaces

import pt.isel.ipw.domain.DTO.output.GetProcessResponse
import pt.isel.ipw.domain.output.CreateProcessResponse
import pt.isel.ipw.domain.process.ProcessView
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.ProcessError

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
    ): Either<ProcessError, Int>


    fun getProcessById(id: Int, userId: Int, role: String): Either<ProcessError, ProcessView>

    fun getAllProcesses(offset: Int?, limit: Int?, userId: Int, role: String): Either<ProcessError, List<ProcessView>>

    fun changeEndDate(processId: Int, endDate: String, userId: Int, role: String): Either<ProcessError, Unit>

    fun assignInvestigator(processId: Int, triatorId: Int, investigatorId: Int): Either<ProcessError, Unit>

    fun assignSupervisor(processId: Int, triatorId: Int, supervisorId: Int): Either<ProcessError, Unit>

    fun changePriority(processId: Int, newPriority: String, userId: Int):Either<ProcessError, Unit>

    fun cancelProcess(processId: Int, userId: Int): Either<ProcessError, Unit>

}