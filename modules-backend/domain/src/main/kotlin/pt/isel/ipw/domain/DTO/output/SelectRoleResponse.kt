package pt.isel.ipw.domain.DTO.output

class SelectRoleResponse(
    val accessToken: TokenResponse,
    val refreshToken: TokenResponse,
    val role: String,
    val areaId: Int?,
    val area: String?
)