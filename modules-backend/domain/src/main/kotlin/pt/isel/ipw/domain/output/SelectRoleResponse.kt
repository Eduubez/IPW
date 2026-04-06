package pt.isel.ipw.domain.output

class SelectRoleResponse(
    val token: String,
    val userId: Int,
    val role: String,
    val expiresAt: String
) {
}