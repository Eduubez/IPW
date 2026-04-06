package pt.isel.ipw.repository.jdbi

import org.jdbi.v3.core.Handle
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
}