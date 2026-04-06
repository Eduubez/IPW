package pt.isel.ipw.repository

import java.time.Instant

interface TokensRepository {
    fun createToken(
        token: String,
        userId: Int,
        activeRole: String?,
        expiresAt: Instant
    )

    fun updateActiveRole(
        token: String,
        activeRole: String
    ): Int
}