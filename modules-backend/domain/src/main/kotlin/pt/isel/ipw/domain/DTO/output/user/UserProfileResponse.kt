package pt.isel.ipw.domain.DTO.output.user

data class UserProfileResponse(
    val id: Int,
    val name: String,
    val email: String,
    val areaId: Int?,
    val area: String?,
    val roles: List<String>
)