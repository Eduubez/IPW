package pt.isel.ipw.http.errors

import pt.isel.ipw.services.errors.ActivityError

private val activityErrorMap = mapOf(
    ActivityError.InvalidOffset to Problem.invalidOffset,
    ActivityError.InvalidLimit to Problem.invalidLimit,
    ActivityError.ProcessNotFound to Problem.processNotFound,
    ActivityError.UserNotFound to Problem.userNotFound
)

fun ActivityError.toHttp(): Pair<Int, Problem> =
    this.status to (activityErrorMap[this] ?: Problem.internalServerError)
