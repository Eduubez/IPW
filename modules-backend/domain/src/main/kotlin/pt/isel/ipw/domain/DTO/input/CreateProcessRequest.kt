package pt.isel.ipw.domain.DTO.input

data class CreateProcessRequest (
    val name: String,
    val street: String,
    val county: String,
    val district: String,
    val latitude: Int? = null,
    val longitude: Int? = null,
    val area: String,
    val priority: String,
    val expiresAt: String,
    val investigatorId: Int,
    val supervisorId: Int,
    val insuranceId: Int? =null,
    val typificationId: Int? = null,
    val canBeFraud: Boolean,
    val note: String? = null,
)

//id do triador obtem-se através do token