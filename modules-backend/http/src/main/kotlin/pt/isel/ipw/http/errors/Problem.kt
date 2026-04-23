package pt.isel.ipw.http.errors

import org.springframework.http.ResponseEntity

class Problem(
    val type: String,
    val message: String,
    val errorCode: ErrorCode
) {

    companion object {
        const val MEDIA_TYPE = "application/problem+json"

        fun response(status: Int, problem: Problem) =
            ResponseEntity.status(status)
                .header("Content-Type", MEDIA_TYPE)
                .body(problem)

        val internalServerError = Problem(
            type = "problems/internal-server-error",
            message = "An unexpected error occurred",
            errorCode = ErrorCode.INTERNAL_ERROR
        )

        val userAlreadyExists = Problem(
            type = "problems/user-already-exists",
            message = "User with this email already exists",
            errorCode = ErrorCode.USER_ALREADY_EXISTS
        )

        val userNotFound = Problem(
            type = "problems/user-not-found",
            message = "User not found",
            errorCode = ErrorCode.USER_NOT_FOUND
        )

        val userNotActive = Problem(
            type = "problems/user-not-active",
            message = "User is not active",
            errorCode = ErrorCode.USER_NOT_ACTIVE
        )

        val insecurePassword = Problem(
            type = "problems/insecure-password",
            message = "Weak password",
            errorCode = ErrorCode.INSECURE_PASSWORD
        )

        val invalidCredentials = Problem(
            type = "problems/invalid-credentials",
            message = "Invalid email or password",
            errorCode = ErrorCode.INVALID_CREDENTIALS
        )

        val invalidRoleSelection = Problem(
            type = "problems/invalid-role-selection",
            message = "Selected role is not valid for this user",
            errorCode = ErrorCode.INVALID_ROLE
        )

        val invalidRoles = Problem(
            type = "problems/invalid-roles",
            message = "One or more roles are invalid",
            errorCode = ErrorCode.INVALID_ROLES
        )

        val invalidToken = Problem(
            type = "problems/invalid-token",
            message = "Token is invalid",
            errorCode = ErrorCode.INVALID_TOKEN
        )

        val refreshTokenNotFound = Problem(
            type = "problems/refresh-token-not-found",
            message = "Refresh token not found",
            errorCode = ErrorCode.REFRESH_NOT_FOUND
        )

        val expiredLoginToken = Problem(
            type = "problems/login-token-expired",
            message = "Login token has expired",
            errorCode = ErrorCode.LOGIN_EXPIRED
        )

        val expiredAccessToken = Problem(
            type = "problems/access-token-expired",
            message = "Access token has expired",
            errorCode = ErrorCode.ACCESS_EXPIRED
        )

        val expiredRefreshToken = Problem(
            type = "problems/refresh-expired",
            message = "Refresh token has expired",
            errorCode = ErrorCode.REFRESH_EXPIRED
        )

        val areaRequired = Problem(
            type = "problems/area-required",
            message = "Area is required for the selected roles",
            errorCode = ErrorCode.AREA_REQUIRED
        )

        val areaNotFound = Problem(
            type = "problems/area-not-found",
            message = "Area not found",
            errorCode = ErrorCode.AREA_NOT_FOUND
        )

        val areaAlreadyHasSupervisor = Problem(
            type = "problems/area-already-has-supervisor",
            message = "This area already has a supervisor",
            errorCode = ErrorCode.AREA_ALREADY_HAS_SUPERVISOR
        )

        val invalidOffset = Problem(
            type = "problems/invalid-offset",
            message = "Offset must be greater than or equal to 0",
            errorCode = ErrorCode.INVALID_OFFSET
        )

        val invalidLimit = Problem(
            type = "problems/invalid-limit",
            message = "Limit must be greater than 0",
            errorCode = ErrorCode.INVALID_LIMIT
        )

        val processNotFound = Problem(
            type = "problems/process-not-found",
            message = "Process not found",
            errorCode = ErrorCode.PROCESS_NOT_FOUND
        )
    }
}