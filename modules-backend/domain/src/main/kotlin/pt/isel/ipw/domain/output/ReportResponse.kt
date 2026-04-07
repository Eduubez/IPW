package pt.isel.ipw.domain.output

data class ReportResponse(
    // Report não deveria ter um id próprio? Proves tem (bd)
    val processId: Int,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
)

