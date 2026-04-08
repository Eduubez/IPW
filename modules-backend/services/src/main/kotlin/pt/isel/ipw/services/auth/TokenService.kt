package pt.isel.ipw.services.auth

interface TokenService {
    fun createAccessToken(
        userId: Int,
        roles: List<String>
    ): CreatedToken

    fun createRefreshToken(userId: Int): CreatedToken

    fun parseAccessToken(token: String): TokenClaims

    fun parseRefreshToken(token: String): Int
}