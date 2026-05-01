package pt.isel.ipw.domain.DTO.output.user

data class AdminUserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val areaId: Int?,
    val area: String?,
    val isActive: Boolean,
    val roles: List<String>
)
