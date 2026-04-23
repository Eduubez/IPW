package pt.isel.ipw.services.auth

import java.time.Instant

data class LoginResult(
    val loginToken: String,
    val userId: Int,
    val roles: List<String>,
    val expiresAt: Instant
)

