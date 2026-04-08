package pt.isel.ipw.services.auth

import java.time.Instant

class RefreshAccessToken(
    val token: String,
    val expiresAt: Instant
) {
}