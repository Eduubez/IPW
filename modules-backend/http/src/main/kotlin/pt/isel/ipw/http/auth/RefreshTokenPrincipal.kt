package pt.isel.ipw.http.auth

import pt.isel.ipw.services.auth.TokenClaims

data class RefreshTokenPrincipal(
    val token: String,
    val claims: TokenClaims
)