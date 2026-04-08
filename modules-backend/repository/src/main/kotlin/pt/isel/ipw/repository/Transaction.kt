package pt.isel.ipw.repository

interface Transaction {
    val usersRepository: UsersRepository
    val tokensRepository: TokensRepository
    val refreshTokensRepository: RefreshTokensRepository

    fun rollback()
}