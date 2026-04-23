package pt.isel.ipw.domain.output

data class LoginResponse(
    val loginToken: TokenResponse,
    val userId: Int,
    val roles: List<String>
)