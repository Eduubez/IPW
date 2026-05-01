package pt.isel.ipw.domain.user

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val passwordHash: String,
    val area: String?,
    val isActive: Boolean
){
}