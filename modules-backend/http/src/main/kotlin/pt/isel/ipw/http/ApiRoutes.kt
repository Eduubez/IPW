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
        const val INVESTIGATORS = "/investigators"
        const val SUPERVISORS = "/supervisors"

        const val LOGIN_FULL = "$BASE$LOGIN"
        const val LOGOUT_FULL = "$BASE$LOGOUT"
        const val REFRESH_TOKEN_FULL = "$BASE$REFRESH_TOKEN"
        const val SELECT_ROLE_FULL = "$BASE$SELECT_ROLE"
        const val ROLES_FULL = "$BASE$ROLES"
        const val INVESTIGATORS_FULL = "$BASE$INVESTIGATORS"
        const val SUPERVISORS_FULL = "$BASE$SUPERVISORS"
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

    object Process {
        const val BASE = "$API/process"
        const val BY_ID = "/{id}"
        const val END_DATE = "$BY_ID/end-date"
        const val INVESTIGATOR = "$BY_ID/investigator"
        const val SUPERVISOR = "$BY_ID/supervisor"
        const val PRIORITY = "$BY_ID/priority"
        const val CANCEL = "$BY_ID/cancel"
        const val PROVES = "$BY_ID/proves"
        const val PROVE_BY_ID = "$PROVES/{proveId}"
        const val PROVE_UPLOAD_URL = "$PROVES/upload-url"
        const val PROVE_ACCESS_URL = "$PROVE_BY_ID/url"
        const val SUBMIT = "$BY_ID/submit"

        const val BY_ID_FULL = "$BASE$BY_ID"
        const val END_DATE_FULL = "$BASE$END_DATE"
        const val INVESTIGATOR_FULL = "$BASE$INVESTIGATOR"
        const val SUPERVISOR_FULL = "$BASE$SUPERVISOR"
        const val PRIORITY_FULL = "$BASE$PRIORITY"
        const val CANCEL_FULL = "$BASE$CANCEL"
        const val PROVES_FULL = "$BASE$PROVES"
        const val PROVE_BY_ID_FULL = "$BASE$PROVE_BY_ID"
        const val PROVE_UPLOAD_URL_FULL = "$BASE$PROVE_UPLOAD_URL"
        const val PROVE_ACCESS_URL_FULL = "$BASE$PROVE_ACCESS_URL"
        const val SUBMIT_FULL = "$BASE$SUBMIT"
    }

    object Report {

        const val BASE = "${Process.BASE}/{processId}/report"

        const val APPROVE = "/approve"
        const val REJECT = "/reject"

    }

}
