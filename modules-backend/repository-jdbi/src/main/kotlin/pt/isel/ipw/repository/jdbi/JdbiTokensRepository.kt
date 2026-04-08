package pt.isel.ipw.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.ipw.domain.AccessToken
import pt.isel.ipw.repository.TokensRepository
import java.sql.Timestamp
import java.time.Instant

class JdbiTokensRepository(
    private val handle: Handle
) : TokensRepository {

    override fun createToken(
        token: String,
        userId: Int,
        activeRole: String?,
        expiresAt: Instant
    ) {
        handle.createUpdate(
        """
                insert into Token(token, user_id, active_role, expires_at)
                values (:token, :userId, :activeRole, :expiresAt)
            """
        )
            .bind("token", token)
            .bind("userId", userId)
            .bind("activeRole", activeRole)
            .bind("expiresAt", Timestamp.from(expiresAt))
            .execute()
    }

    override fun updateActiveRole(token: String, activeRole: String): Int {
        return handle.createUpdate(
        """
                update Token
                set active_role = :activeRole
                where token = :token
            """
        )
            .bind("token", token)
            .bind("activeRole", activeRole)
            .execute()
    }

    override fun deleteAccessTokens(userId: Int): Int {
        return handle.createUpdate(
        """
                delete from Token
                where user_id = :userId
            """
        )
            .bind("userId", userId)
            .execute()
    }

    override fun getAccessToken(userId: Int): AccessToken? {
        return handle.createQuery(
        """
                select token, user_id, active_role, created_at, expires_at
                from Token
                where user_id = :userId
            """
        )
            .bind("userId", userId)
            .mapTo(AccessToken::class.java)
            .findOne()
            .orElse(null)
    }
}