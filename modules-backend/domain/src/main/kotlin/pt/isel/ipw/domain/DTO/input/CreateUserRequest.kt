package pt.isel.ipw.domain.DTO.input

data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val areaId: Int?,
    val roles: List<String>
)