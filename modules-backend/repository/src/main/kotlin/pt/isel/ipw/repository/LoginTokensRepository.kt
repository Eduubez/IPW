package pt.isel.ipw.repository

import pt.isel.ipw.domain.LoginToken
import java.time.Instant

interface LoginTokensRepository {

    fun create(
        token: String,
        userId: Int,
        expiresAt: Instant
    )

    fun getByToken(token: String): LoginToken?

    fun deleteByToken(token: String)

    fun deleteByUserId(userId: Int)
}