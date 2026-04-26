package pt.isel.ipw.http

object ApiRoutes {
    object Users {
        const val BASE = "/users"
        const val LOGIN = "/login"
        const val REFRESH_TOKEN = "/refresh-token"
        const val SELECT_ROLE = "/auth/select-role"
        const val ROLES = "/roles"

        const val LOGIN_FULL = "$BASE$LOGIN"
        const val REFRESH_TOKEN_FULL = "$BASE$REFRESH_TOKEN"
        const val SELECT_ROLE_FULL = "$BASE$SELECT_ROLE"
        const val ROLES_FULL = "$BASE$ROLES"
    }
}