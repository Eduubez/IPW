package pt.isel.ipw.repository.jdbi.transaction

import org.jdbi.v3.core.Handle
import pt.isel.ipw.repository.IHistoryRepository
import pt.isel.ipw.repository.RefreshTokensRepository
import pt.isel.ipw.repository.Transaction
import pt.isel.ipw.repository.jdbi.JdbiRefreshTokensRepository
import pt.isel.ipw.repository.jdbi.JdbiTokensRepository
import pt.isel.ipw.repository.jdbi.JdbiUsersRepository
import pt.isel.ipw.repository.jdbi.history.JdbiHistoryRepository

class JdbiTransaction(
    private val handle: Handle
) : Transaction {

    override val usersRepository by lazy { JdbiUsersRepository(handle) }
    override val tokensRepository by lazy { JdbiTokensRepository(handle) }
    override val refreshTokensRepository by lazy { JdbiRefreshTokensRepository(handle) }
    override val historyRepository by lazy { JdbiHistoryRepository(handle) }
    override fun rollback() {
        handle.rollback()
    }

}