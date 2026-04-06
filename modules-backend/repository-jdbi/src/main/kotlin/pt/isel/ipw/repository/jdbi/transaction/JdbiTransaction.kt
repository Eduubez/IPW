package pt.isel.ipw.repository.jdbi.transaction

import org.jdbi.v3.core.Handle
import pt.isel.ipw.repository.Transaction
import pt.isel.ipw.repository.jdbi.JdbiTokensRepository
import pt.isel.ipw.repository.jdbi.JdbiUsersRepository

class JdbiTransaction(
    private val handle: Handle
) : Transaction {

    override val usersRepository by lazy { JdbiUsersRepository(handle) }
    override val tokensRepository by lazy { JdbiTokensRepository(handle) }

}