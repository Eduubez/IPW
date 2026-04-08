package pt.isel.ipw.repository

import pt.isel.ipw.domain.RefreshToken
import java.time.Instant

interface RefreshTokensRepository {

    fun createRefreshToken(
        token: String,
        userId: Int,
        expiresAt: Instant
    )

    fun getRefreshToken(token: String): RefreshToken?

    fun deleteRefreshTokens(userId: Int): Int
}