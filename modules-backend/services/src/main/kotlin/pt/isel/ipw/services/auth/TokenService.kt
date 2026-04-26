package pt.isel.ipw.services.auth

interface TokenService {
    fun createLoginToken(userId: Int, roles: List<String>): CreatedToken
    fun createAccessToken(userId: Int, role: String): CreatedToken
    fun createRefreshToken(userId: Int, role: String): CreatedToken

    fun parseLoginToken(token: String): LoginTokenClaims
    fun parseAccessToken(token: String): TokenClaims
    fun parseRefreshToken(token: String): TokenClaims

    fun isValid(token: String): Boolean
}