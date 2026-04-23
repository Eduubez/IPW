package pt.isel.ipw.repository.jdbi.tokens

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.ipw.domain.LoginToken
import pt.isel.ipw.repository.LoginTokensRepository
import java.time.Instant

class JdbiLoginTokensRepository(
    private val handle: Handle
) : LoginTokensRepository {

    override fun create(token: String, userId: Int, expiresAt: Instant) {
        handle.createUpdate(
            """
            insert into LoginToken(token, user_id, expires_at)
            values (:token, :userId, :expiresAt)
            """
        )
            .bind("token", token)
            .bind("userId", userId)
            .bind("expiresAt", expiresAt)
            .execute()
    }

    override fun getByToken(token: String): LoginToken? {
        return handle.createQuery(
            """
                    select token, user_id, created_at, expires_at
                    from LoginToken
                    where token = :token
                """
        )
            .bind("token", token)
            .mapTo<LoginToken>()
            .findOne()
            .orElse(null)
    }

    override fun deleteByToken(token: String) {
        handle.createUpdate(
            """
            delete from LoginToken
            where token = :token
            """
        )
            .bind("token", token)
            .execute()
    }

    override fun deleteByUserId(userId: Int) {
        handle.createUpdate(
            """
            delete from LoginToken
            where user_id = :userId
            """
        )
            .bind("userId", userId)
            .execute()
    }
}
