package pt.isel.ipw.http.auth

import pt.isel.ipw.services.auth.LoginTokenClaims

data class LoginTokenPrincipal(
    val token: String,
    val claims: LoginTokenClaims
)