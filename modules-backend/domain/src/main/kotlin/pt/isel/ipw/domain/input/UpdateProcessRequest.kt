package pt.isel.ipw.domain.input

data class UpdateProcessRequest (
    val name: String = "",
    val street: String = "",
    val county: String = "",
    val district: String = "",
    val expiresAt: String = "",
    val investigatorName: String = "",
    val priority: String = "",
    val canBeFraud: Boolean? = null,
    val state: String = "",
)

// strings iniciadas para casos em que apenas seja necessário trocar uma propriedade
// diferença entre Create e Update Process é que não dá para mudar area nem supervisor

// serve tambem para os endpoint de mudar priority e state