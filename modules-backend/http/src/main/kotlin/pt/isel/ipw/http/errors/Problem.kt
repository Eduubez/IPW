package pt.isel.ipw.http.errors

import org.springframework.http.ResponseEntity

class Problem(type: String) {
    val type = "problems/$type"

    companion object {
        const val MEDIA_TYPE = "application/problem+json"

        fun response(status: Int, problem: Problem) =
            ResponseEntity.status(status)
                .header("Content-Type", MEDIA_TYPE)
                .body(problem)

        val internalServerError = Problem("internal-server-error")
        val userAlreadyExists = Problem("user-already-exists")
        val insecurePassword = Problem("insecure-password")
        val invalidCredentials = Problem("invalid-credentials")
        val invalidRoleSelection = Problem("invalid-role-selection")
        val invalidToken = Problem("invalid-token")
        val refreshTokenNotFound = Problem("refresh-token-not-found")
        val expiredRefreshToken = Problem("refresh-expired")
    }
}