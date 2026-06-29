package pt.isel.ipw.repository.jdbi.transaction

import org.jdbi.v3.core.Handle
import pt.isel.ipw.repository.Transaction
import pt.isel.ipw.repository.jdbi.JdbiReportRepository
import pt.isel.ipw.repository.jdbi.activity.JdbiActivityRepository
import pt.isel.ipw.repository.jdbi.area.JdbiAreasRepository
import pt.isel.ipw.repository.jdbi.note.JdbiNoteRepository
import pt.isel.ipw.repository.jdbi.process.JdbiProcessRepository
import pt.isel.ipw.repository.jdbi.proves.JdbiProvesRepository
import pt.isel.ipw.repository.jdbi.tokens.JdbiAccessTokensRepository
import pt.isel.ipw.repository.jdbi.tokens.JdbiLoginTokensRepository
import pt.isel.ipw.repository.jdbi.tokens.JdbiRefreshTokensRepository
import pt.isel.ipw.repository.jdbi.user.JdbiUsersRepository


class JdbiTransaction(
    private val handle: Handle
) : Transaction {
    override val usersRepository by lazy { JdbiUsersRepository(handle) }

    override val loginTokensRepository by lazy { JdbiLoginTokensRepository(handle) }
    override val accessTokensRepository by lazy { JdbiAccessTokensRepository(handle) }
    override val refreshTokensRepository by lazy { JdbiRefreshTokensRepository(handle) }

    override val areasRepository by lazy { JdbiAreasRepository(handle) }
    override val activityRepository by lazy { JdbiActivityRepository(handle) }

    override val processRepository by lazy { JdbiProcessRepository(handle) }
    override val provesRepository by lazy { JdbiProvesRepository(handle) }
    override val reportRepository by lazy { JdbiReportRepository(handle) }
    override val noteRepository by lazy { JdbiNoteRepository(handle) }


    override fun rollback() {
        handle.rollback()
    }

}
