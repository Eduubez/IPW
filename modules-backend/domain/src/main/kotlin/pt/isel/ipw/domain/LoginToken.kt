package pt.isel.ipw.domain

import java.time.Instant

class LoginToken(
    val token: String,
    val userId: Int,
    val createdAt: Instant,
    val expiresAt: Instant,
) {
}