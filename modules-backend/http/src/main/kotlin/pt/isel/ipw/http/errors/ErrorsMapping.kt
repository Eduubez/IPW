package pt.isel.ipw.http.errors

import pt.isel.ipw.services.errors.UserError

private val userErrorMap = mapOf(
    UserError.UserAlreadyExists to Problem.userAlreadyExists,
    UserError.InsecurePassword to Problem.insecurePassword,
    UserError.InvalidCredentials to Problem.invalidCredentials,
    UserError.InvalidRoleSelection to Problem.invalidRoleSelection,
    UserError.InvalidToken to Problem.invalidToken,
    UserError.RefreshTokenNotFound to Problem.refreshTokenNotFound,
    UserError.ExpiredRefreshToken to Problem.expiredRefreshToken
)

fun UserError.toHttp(): Pair<Int, Problem> =
    this.status to (userErrorMap[this] ?: Problem.internalServerError)