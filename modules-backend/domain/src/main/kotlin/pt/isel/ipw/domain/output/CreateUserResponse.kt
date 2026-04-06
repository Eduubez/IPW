package pt.isel.ipw.domain.output

data class CreateUserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val areaId: Int?,
    val roles: List<String>
)