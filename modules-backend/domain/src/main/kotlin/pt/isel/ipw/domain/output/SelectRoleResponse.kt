package pt.isel.ipw.domain.output

class SelectRoleResponse(
    val accessToken: TokenResponse,
    val refreshToken: TokenResponse,
    val role: String
)