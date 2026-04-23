package pt.isel.ipw.services.errors

sealed class UserError(
    val status: Int
) {
    data object UserNotFound : UserError(404)
    data object UserAlreadyExists : UserError(409)

    data object InsecurePassword : UserError(400)
    data object InvalidCredentials : UserError(401)

    data object InvalidRoles : UserError(400)
    data object InvalidRoleSelection : UserError(400)

    data object InvalidToken : UserError(401)
    data object ExpiredLoginToken : UserError(403)
    data object ExpiredAccessToken : UserError(401)
    data object ExpiredRefreshToken : UserError(401)
    data object RefreshTokenNotFound : UserError(404)

    data object UserNotActive : UserError(403)

    data object AreaRequired : UserError(400)
    data object AreaNotFound : UserError(404)
    data object AreaAlreadyHasSupervisor : UserError(409)
}