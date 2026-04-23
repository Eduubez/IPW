package pt.isel.ipw.repository

import pt.isel.ipw.domain.AccessToken
import java.time.Instant

interface AccessTokensRepository {
    fun create(
        token: String,
        userId: Int,
        role: String,
        expiresAt: Instant
    )

    fun getByToken(token: String): AccessToken?

    fun getByUserId(userId: Int): AccessToken?

    fun deleteByToken(token: String)

    fun deleteByUserId(userId: Int)
}