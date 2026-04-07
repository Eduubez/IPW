package pt.isel.ipw.domain.input

data class CreateProcessRequest (
    val name: String,
    val street: String,
    val county: String,
    val district: String,
    val area: String,
    val priority: String,
    val expiresAt: String,
    val investigatorName: String,  //precisamos de um getUserByName
    val supervisor: String,
    val canBeFraud: Boolean,
)

//id do triador obtem-se através do token