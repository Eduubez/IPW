package pt.isel.ipw.domain.output

data class LoginResponse(
    val token: TokenResponse,
    val userId: Int,
    val roles: List<String>
)