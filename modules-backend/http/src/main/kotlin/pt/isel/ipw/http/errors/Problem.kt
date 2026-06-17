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

        val cannotDeactivateSelf = Problem(
            type = "problems/cannot-deactivate-self",
            message = "Admin cannot deactivate their own user",
            errorCode = ErrorCode.CANNOT_DEACTIVATE_SELF
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

        val invalidProcessName = Problem(
            type = "problems/invalid-process-name",
            message = "Process name must have at least 3 characters",
            errorCode = ErrorCode.INVALID_PROCESS_NAME
        )

        val invalidLocation = Problem(
            type = "problems/invalid-location",
            message = "Location must have all fields filled and valid",
            errorCode = ErrorCode.INVALID_LOCATION
        )

        val invalidExpirationDate = Problem(
            type = "problems/invalid-expiration-date",
            message = "The expiration date is not valid",
            errorCode = ErrorCode.INVALID_EXPIRATION_DATE
        )

        val invalidPriority = Problem(
            type = "problems/invalid-priority",
            message = "The priority is not valid",
            errorCode = ErrorCode.INVALID_PRIORITY
        )

        val invalidInvestigator = Problem(
            type = "problems/invalid-investigator",
            message = "Investigator does not exist or does not belong to the same area",
            errorCode = ErrorCode.INVALID_INVESTIGATOR
        )

        val invalidSupervisor = Problem(
            type = "problems/invalid-supervisor",
            message = "Supervisor does not exist or does not belong to the same area",
            errorCode = ErrorCode.INVALID_SUPERVISOR
        )

        val invalidTriator = Problem(
            type = "problems/invalid-triator",
            message = "Triator is not allowed to create a new process",
            errorCode = ErrorCode.INVALID_TRIATOR
        )

        val invalidInsurance = Problem(
            type = "problems/invalid-insurance",
            message = "Insurance does not exist.",
            errorCode = ErrorCode.INVALID_INSURANCE
        )

        val invalidTypification = Problem(
            type = "problems/invalid-typification",
            message = "Typification does not exist.",
            errorCode = ErrorCode.INVALID_TYPIFICATION
        )

        val updateNotAllowed = Problem(
            type = "problems/update-not-allowed",
            message = "Update is not allowed.",
            errorCode = ErrorCode.UPDATE_NOT_ALLOWED
        )

        val invalidUserId = Problem(
            type = "problems/invalid-user-id",
            message = "Invalid userId.",
            errorCode = ErrorCode.INVALID_USER_ID
        )

        val invalidAreaId = Problem(
            type = "problems/invalid-area-id",
            message = "Area id must be positive.",
            errorCode = ErrorCode.INVALID_AREA_ID
        )

        val invalidContent =
            Problem(
                type = "problems/invalid-content",
                message = "Report content must be between 10 and 1000 characters",
                errorCode = ErrorCode.INVALID_CONTENT
            )

        val notAssociated =
            Problem(
                type = "problems/not-associated",
                message = "Not associated.",
                errorCode = ErrorCode.NOT_ASSOCIATED
            )

        val unauthorizedInvestigator =
            Problem(
                type = "problems/unauthorized-investigator",
                message = "Investigator does not belongs to this process",
                errorCode = ErrorCode.UNAUTHORIZED_INVESTIGATOR
            )


        val unauthorized =
            Problem(
                type = "problems/unauthorized",
                message = "User does not have permission to perform this action",
                errorCode = ErrorCode.UNAUTHORIZED
            )

        val reportNotFound =
            Problem(
                type = "problems/report-not-found",
                message = "Report not found for the given process",
                errorCode = ErrorCode.REPORT_NOT_FOUND
            )


        val alreadyRejected =
            Problem(
                type = "problems/already-rejected",
                message = "The process was already rejected",
                errorCode = ErrorCode.ALREADY_REJECTED
            )

        val notApprovedBySupervisor =
            Problem(
                type = "problems/not-approved-by-supervisor",
                message = "The process was not approved by a supervisor",
                errorCode = ErrorCode.NOT_APPROVED_BY_SUPERVISOR
            )

        val alreadyApproved =
            Problem(
                type = "problems/already-approved",
                message = "The process was already approved",
                errorCode = ErrorCode.ALREADY_APPROVED
            )

        val proveNotFound = Problem(
            type = "problems/prove-not-found",
            message = "Prove not found",
            errorCode = ErrorCode.PROVE_NOT_FOUND
        )

        val invalidFileName = Problem(
            type = "problems/invalid-file-name",
            message = "Invalid file name",
            errorCode = ErrorCode.INVALID_FILE_NAME
        )

        val invalidContentType = Problem(
            type = "problems/invalid-content-type",
            message = "Invalid content type",
            errorCode = ErrorCode.INVALID_CONTENT_TYPE
        )

        val invalidFileSize = Problem(
            type = "problems/invalid-file-size",
            message = "Invalid file size",
            errorCode = ErrorCode.INVALID_FILE_SIZE
        )

        val invalidStorageKey = Problem(
            type = "problems/invalid-storage-key",
            message = "Invalid storage key",
            errorCode = ErrorCode.INVALID_STORAGE_KEY
        )

        val storageError = Problem(
            type = "problems/storage-error",
            message = "Storage operation failed",
            errorCode = ErrorCode.STORAGE_ERROR
        )
    }
}
