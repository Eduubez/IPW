package pt.isel.ipw.domain.DTO.input

data class UpdateProcessRequest (
    val name: String ,
    val street: String,
    val county: String,
    val district: String,
    val expiresAt: String,
    val investigatorName: String,
    val priority: String,
    val canBeFraud: Boolean,
    val state: String ,
)
