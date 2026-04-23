package pt.isel.ipw.domain

import java.time.Instant

data class RefreshToken(
    val token: String,
    val userId: Int,
    val role: String,
    val createdAt: Instant,
    val expiresAt: Instant,
){
}