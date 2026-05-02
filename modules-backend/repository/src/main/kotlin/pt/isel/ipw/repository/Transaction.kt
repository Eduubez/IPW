package pt.isel.ipw.repository

interface Transaction {
    val usersRepository: UsersRepository

    val loginTokensRepository: LoginTokensRepository
    val accessTokensRepository: AccessTokensRepository
    val refreshTokensRepository: RefreshTokensRepository

    val areasRepository: AreasRepository
    val activityRepository: ActivityRepository
    val historyRepository: HistoryRepository
    val processRepository: ProcessRepository

    fun rollback()
}