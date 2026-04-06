package pt.isel.ipw.domain.output

data class LoginResponse(
    val token: String,
    val userId: Int,
    val roles: List<String>,
    val expiresAt: String
)