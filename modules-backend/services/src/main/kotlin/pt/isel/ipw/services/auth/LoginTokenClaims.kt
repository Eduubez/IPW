package pt.isel.ipw.services.auth

data class  LoginTokenClaims(
    val userId: Int,
    val roles: List<String>
)