package pt.isel.ipw.services.interfaces

import pt.isel.ipw.services.results.CreateActivityResult
import pt.isel.ipw.services.results.GetActivitiesByProcessResult
import pt.isel.ipw.services.results.GetActivitiesByUserResult

interface ActivityService {
    fun getActivitiesByProcess(
        processId: Int,
        offset: Int,
        limit: Int
    ): GetActivitiesByProcessResult

    fun getActivitiesByUser(
        userId: Int,
        offset: Int,
        limit: Int
    ): GetActivitiesByUserResult

    fun createActivity(
        processId: Int,
        userId: Int,
        action: String,
        description: String,
    ): CreateActivityResult
}
