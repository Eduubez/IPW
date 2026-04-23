package pt.isel.ipw.repository.jdbi.tokens

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.ipw.domain.RefreshToken
import pt.isel.ipw.repository.RefreshTokensRepository
import java.sql.Timestamp
import java.time.Instant

class JdbiRefreshTokensRepository(
    private val handle: Handle
) : RefreshTokensRepository {

    override fun create(
        token: String,
        userId: Int,
        role: String,
        expiresAt: Instant
    ) {
        handle.createUpdate(
            """
            insert into RefreshToken(token, user_id, role, expires_at)
            values (:token, :userId, :role, :expiresAt)
            """
        )
            .bind("token", token)
            .bind("userId", userId)
            .bind("role", role)
            .bind("expiresAt", expiresAt)
            .execute()
    }


    override fun getByToken(token: String): RefreshToken? {
        return handle.createQuery(
            """
            select token, user_id, role, created_at, expires_at
            from RefreshToken
            where token = :token
            """
        )
            .bind("token", token)
            .mapTo<RefreshToken>()
            .findOne()
            .orElse(null)
    }

    override fun getByUserId(userId: Int): RefreshToken? {
        return handle.createQuery(
            """
            select token, user_id, role, created_at, expires_at
            from RefreshToken
            where user_id = :userId
            """
        )
            .bind("userId", userId)
            .mapTo<RefreshToken>()
            .findOne()
            .orElse(null)
    }

    override fun deleteByToken(token: String) {
        handle.createUpdate(
            """
            delete from RefreshToken
            where token = :token
            """
        )
            .bind("token", token)
            .execute()
    }

    override fun deleteByUserId(userId: Int) {
        handle.createUpdate(
            """
            delete from RefreshToken
            where user_id = :userId
            """
        )
            .bind("userId", userId)
            .execute()
    }


}