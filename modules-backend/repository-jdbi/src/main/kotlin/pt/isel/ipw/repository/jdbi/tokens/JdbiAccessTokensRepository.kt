package pt.isel.ipw.repository.jdbi.tokens

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.ipw.domain.AccessToken
import pt.isel.ipw.repository.AccessTokensRepository
import java.time.Instant

class JdbiAccessTokensRepository(
    private val handle: Handle
) : AccessTokensRepository {

    override fun create(
        token: String,
        userId: Int,
        role: String,
        expiresAt: Instant
    ) {
        handle.createUpdate(
            """
            insert into Accesstoken(token, user_id, role, expires_at)
            values (:token, :userId, :role, :expiresAt)
            """
        )
            .bind("token", token)
            .bind("userId", userId)
            .bind("role", role)
            .bind("expiresAt", expiresAt)
            .execute()
    }

    override fun getByToken(token: String): AccessToken? {
        return handle.createQuery(
            """
            select token, user_id, role, created_at, expires_at
            from Accesstoken
            where token = :token
            """
        )
            .bind("token", token)
            .mapTo<AccessToken>()
            .findOne()
            .orElse(null)
    }

    override fun getByUserId(userId: Int): AccessToken? {
        return handle.createQuery(
            """
            select token, user_id, role, created_at, expires_at
            from Accesstoken
            where user_id = :userId
            """
        )
            .bind("userId", userId)
            .mapTo<AccessToken>()
            .findOne()
            .orElse(null)
    }

    override fun deleteByToken(token: String) {
        handle.createUpdate(
            """
            delete from Accesstoken
            where token = :token
            """
        )
            .bind("token", token)
            .execute()
    }

    override fun deleteByUserId(userId: Int) {
        handle.createUpdate(
            """
            delete from Accesstoken
            where user_id = :userId
            """
        )
            .bind("userId", userId)
            .execute()
    }


}