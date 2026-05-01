package pt.isel.ipw.domain.DTO.input

data class ChangeUserRolesRequest(
    val roles: List<String>,
    val areaId: Int?
)