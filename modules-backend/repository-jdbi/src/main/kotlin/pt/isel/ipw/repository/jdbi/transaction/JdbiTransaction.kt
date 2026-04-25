package pt.isel.ipw.repository.jdbi.transaction

import org.jdbi.v3.core.Handle
import pt.isel.ipw.repository.AreasRepository
import pt.isel.ipw.repository.Transaction
import pt.isel.ipw.repository.jdbi.JdbiActivityRepository
import pt.isel.ipw.repository.jdbi.JdbiUsersRepository
import pt.isel.ipw.repository.jdbi.area.JdbiAreasRepository
import pt.isel.ipw.repository.jdbi.history.JdbiHistoryRepository
import pt.isel.ipw.repository.jdbi.tokens.JdbiAccessTokensRepository
import pt.isel.ipw.repository.jdbi.tokens.JdbiLoginTokensRepository
import pt.isel.ipw.repository.jdbi.tokens.JdbiRefreshTokensRepository


class JdbiTransaction(
    private val handle: Handle
) : Transaction {
    override val usersRepository by lazy { JdbiUsersRepository(handle) }

    override val loginTokensRepository by lazy { JdbiLoginTokensRepository(handle) }
    override val accessTokensRepository by lazy { JdbiAccessTokensRepository(handle) }
    override val refreshTokensRepository by lazy { JdbiRefreshTokensRepository(handle) }

    override val areasRepository by lazy { JdbiAreasRepository(handle) }
    override val activityRepository by lazy { JdbiActivityRepository(handle) }
    override val historyRepository by lazy { JdbiHistoryRepository(handle) }
    override fun rollback() {
        handle.rollback()
    }

}