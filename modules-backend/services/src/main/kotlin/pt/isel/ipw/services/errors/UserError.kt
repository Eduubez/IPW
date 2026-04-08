package pt.isel.ipw.services.errors

sealed class UserError(
    val code: String,
    val status: Int
) {
    data object UserNotFound : UserError("USER_NOT_FOUND", 404)
    data object UserAlreadyExists : UserError("USER_ALREADY_EXISTS", 409)
    data object InsecurePassword : UserError("INSECURE_PASSWORD", 400)
    data object InvalidCredentials : UserError("INVALID_CREDENTIALS", 401)
    data object InvalidRoles : UserError("INVALID_ROLES", 400)
    data object InvalidRoleSelection : UserError("INVALID_ROLE_SELECTION", 400)
    data object InvalidToken : UserError("INVALID_TOKEN", 401)
    data object ExpiredRefreshToken : UserError("EXPIRED_REFRESH_TOKEN", 401)
    data object RefreshTokenNotFound : UserError("REFRESH_TOKEN_NOT_FOUND", 404)
    data object UserNotActive : UserError("USER_NOT_ACTIVE", 401)
}