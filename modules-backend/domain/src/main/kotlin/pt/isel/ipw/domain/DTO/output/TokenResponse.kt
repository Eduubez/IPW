package pt.isel.ipw.domain.DTO.output

data class TokenResponse(
    val value: String,
    val expiresAt: String
){
}