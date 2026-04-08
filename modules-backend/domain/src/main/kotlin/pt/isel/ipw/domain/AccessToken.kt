package pt.isel.ipw.domain

import java.time.Instant

data class AccessToken(
    val token: String,
    val userId: Int,
    val activeRole: String?,
    val createdAt: Instant,
    val expiresAt: Instant
){
}