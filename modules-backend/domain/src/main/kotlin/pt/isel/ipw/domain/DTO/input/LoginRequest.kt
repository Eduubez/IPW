package pt.isel.ipw.domain.DTO.input

data class LoginRequest(
    val email: String,
    val password: String
)