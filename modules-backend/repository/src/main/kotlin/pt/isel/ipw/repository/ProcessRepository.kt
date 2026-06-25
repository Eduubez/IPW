package pt.isel.ipw.repository

import pt.isel.ipw.domain.process.ProcessView

interface ProcessRepository {
    fun createProcess(
        triatorId: Int,
        name: String,
        street: String,
        county: String,
        district: String,
        latitude: Int? = null,
        longitude: Int? = null,
        area: String,
        priority: String,
        expiresAt: String,
        investigatorId: Int?,
        supervisorId: Int?,
        insuranceId: Int?,
        typificationId: Int?,
        canBeFraud: Boolean,
        note: String? = null
    ): Int

    fun getById(id: Int): ProcessView?


    fun getAll(
        offset: Int,
        limit: Int,
        areaId: Int,
        userId: Int,
        role:String,
        processStates: List<String>
    ): List<ProcessView>



    fun updateEndDate(processId: Int, endDate: String)

    fun updateProcessInvestigator(processId: Int, investigatorId: Int)

    fun updateProcessSupervisor(processId: Int, supervisorId: Int)

    fun updateProcessPriority(processId: Int, newPriority: String)

    fun cancelProcess(processId: Int)

    fun changeState(processId: Int, newState: String)


}