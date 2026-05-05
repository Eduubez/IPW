package pt.isel.ipw.services.interfaces

import pt.isel.ipw.domain.Activity
import pt.isel.ipw.services.errors.ActivityError
import pt.isel.ipw.services.errors.Either
import java.time.LocalDateTime

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

    fun createActivity(
        processId: Int,
        userId: Int,
        action: String,
        description: String,
    ): Either<ActivityError, Int>
}