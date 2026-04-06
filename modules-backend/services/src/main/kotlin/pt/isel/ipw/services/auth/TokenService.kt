package pt.isel.ipw.services.auth

interface TokenService {
    fun createLoginToken(
        userId: Int,
        roles: List<String>
    ): CreatedToken

    fun parseToken(token: String): TokenClaims
}