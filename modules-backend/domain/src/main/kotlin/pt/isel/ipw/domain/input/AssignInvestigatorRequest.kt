package pt.isel.ipw.domain.input

data class AssignInvestigatorRequest (
    val processId: Int,
    val investigatorName: String,
)