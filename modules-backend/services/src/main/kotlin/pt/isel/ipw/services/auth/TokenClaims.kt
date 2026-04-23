package pt.isel.ipw.services.auth

data class TokenClaims(
    val userId: Int,
    val role: String
) {
}