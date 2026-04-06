package pt.isel.ipw.domain.input

data class LoginRequest(
    val email: String,
    val password: String
)