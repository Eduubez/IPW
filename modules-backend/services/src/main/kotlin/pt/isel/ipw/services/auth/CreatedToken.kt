package pt.isel.ipw.services.auth

import java.time.Instant

class CreatedToken(
    val token: String,
    val expiresAt: Instant
)