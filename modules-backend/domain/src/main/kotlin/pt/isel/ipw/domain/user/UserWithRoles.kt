package pt.isel.ipw.domain.user

data class UserWithRoles(
    val id: Int,
    val name: String,
    val email: String,
    val areaId: Int?,
    val area: String?,
    val isActive: Boolean,
    val roles: List<String>
)
