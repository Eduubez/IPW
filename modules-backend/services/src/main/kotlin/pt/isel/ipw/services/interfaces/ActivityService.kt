package pt.isel.ipw.services.interfaces

import pt.isel.ipw.domain.Activity
import pt.isel.ipw.services.errors.ActivityError
import pt.isel.ipw.services.errors.Either

interface ActivityService {
    fun getActivitiesByProcess(
        processId: Int,
        offset: Int,
        limit: Int
    ): Either<ActivityError, List<Activity>>

    fun getActivitiesByUser(
        userId: Int,
        offset: Int,
        limit: Int
    ): Either<ActivityError, List<Activity>>
}