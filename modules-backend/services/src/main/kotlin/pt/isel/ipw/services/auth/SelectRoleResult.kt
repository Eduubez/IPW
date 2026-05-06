package pt.isel.ipw.services.auth

import java.time.Instant

data class SelectRoleResult(
    val accessToken: String,
    val accessTokenExpiresAt: Instant,
    val refreshToken: String,
    val refreshTokenExpiresAt: Instant,
    val role: String,
    val areaId: Int?,
    val area: String?,
){
}