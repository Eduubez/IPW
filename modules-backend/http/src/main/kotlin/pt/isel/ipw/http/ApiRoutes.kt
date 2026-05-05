package pt.isel.ipw.http

object ApiRoutes {
    const val API = "/api"

    object Users {
        const val BASE = "$API/users"
        const val LOGIN = "/login"
        const val LOGOUT = "/logout"
        const val REFRESH_TOKEN = "/refresh-token"
        const val SELECT_ROLE = "/auth/select-role"
        const val ROLES = "/roles"

        const val LOGIN_FULL = "$BASE$LOGIN"
        const val LOGOUT_FULL = "$BASE$LOGOUT"
        const val REFRESH_TOKEN_FULL = "$BASE$REFRESH_TOKEN"
        const val SELECT_ROLE_FULL = "$BASE$SELECT_ROLE"
        const val ROLES_FULL = "$BASE$ROLES"
    }

    object Activity {
        const val BASE = "$API/activity"
    }

    object Area {
        const val BASE = "$API/area"
    }

    object History {
        const val BASE = "$API/history"
    }
}
