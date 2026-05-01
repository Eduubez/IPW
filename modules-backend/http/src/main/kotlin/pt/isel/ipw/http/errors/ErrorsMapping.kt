package pt.isel.ipw.http.errors

import pt.isel.ipw.services.errors.ActivityError
import pt.isel.ipw.services.errors.UserError

private val userErrorMap = mapOf(
    UserError.UserAlreadyExists to Problem.userAlreadyExists,
    UserError.UserNotFound to Problem.userNotFound,
    UserError.UserNotActive to Problem.userNotActive,
    UserError.InsecurePassword to Problem.insecurePassword,
    UserError.InvalidCredentials to Problem.invalidCredentials,
    UserError.InvalidRoleSelection to Problem.invalidRoleSelection,
    UserError.InvalidRoles to Problem.invalidRoles,
    UserError.InvalidToken to Problem.invalidToken,
    UserError.RefreshTokenNotFound to Problem.refreshTokenNotFound,
    UserError.ExpiredLoginToken to Problem.expiredLoginToken,
    UserError.ExpiredAccessToken to Problem.expiredAccessToken,
    UserError.ExpiredRefreshToken to Problem.expiredRefreshToken,
    UserError.AreaRequired to Problem.areaRequired,
    UserError.AreaNotFound to Problem.areaNotFound,
    UserError.AreaAlreadyHasSupervisor to Problem.areaAlreadyHasSupervisor
)

fun UserError.toHttp(): Pair<Int, Problem> =
    this.status to (userErrorMap[this] ?: Problem.internalServerError)

private val activityErrorMap = mapOf(
    ActivityError.InvalidOffset to Problem.invalidOffset,
    ActivityError.InvalidLimit to Problem.invalidLimit,
    ActivityError.ProcessNotFound to Problem.processNotFound,
    ActivityError.UserNotFound to Problem.userNotFound
)

fun ActivityError.toHttp(): Pair<Int, Problem> =
    this.status to (activityErrorMap[this] ?: Problem.internalServerError)
