package pt.isel.ipw.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.ipw.domain.RefreshToken
import pt.isel.ipw.repository.RefreshTokensRepository
import java.time.Instant
import java.sql.Timestamp

class JdbiRefreshTokensRepository(
    private val handle: Handle
) : RefreshTokensRepository {

    override fun createRefreshToken(
        token: String,
        userId: Int,
        expiresAt: Instant
    ) {
        handle.createUpdate(
            """
            insert into RefreshToken(token, user_id, expires_at)
            values (:token, :userId, :expiresAt)
            """
        )
            .bind("token", token)
            .bind("userId", userId)
            .bind("expiresAt", Timestamp.from(expiresAt))
            .execute()
    }

    override fun getRefreshToken(token: String): RefreshToken? {
        return handle.createQuery(
            """
                    select token, user_id, created_at, expires_at
                    from RefreshToken
                    where token = :token
                """
        )
            .bind("token", token)
            .mapTo(RefreshToken::class.java)
            .findOne()
            .orElse(null)
    }

    override fun deleteRefreshTokens(userId: Int): Int {
        return handle.createUpdate(
            """
                    delete from RefreshToken
                    where user_id = :userId
                """
        )
            .bind("userId", userId)
            .execute()
    }

}