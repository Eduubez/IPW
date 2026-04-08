package pt.isel.ipw.repository

import pt.isel.ipw.domain.AccessToken
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

    fun getAccessToken(userId: Int): AccessToken?

    fun deleteAccessTokens(userId: Int): Int
}