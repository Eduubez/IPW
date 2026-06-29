package pt.isel.ipw.repository

interface Transaction {
    val usersRepository: UsersRepository

    val loginTokensRepository: LoginTokensRepository
    val accessTokensRepository: AccessTokensRepository
    val refreshTokensRepository: RefreshTokensRepository

    val areasRepository: AreasRepository
    val activityRepository: ActivityRepository
    val processRepository: ProcessRepository
    val provesRepository: ProvesRepository
    val reportRepository: ReportRepository
    val noteRepository: NoteRepository

    fun rollback()
}