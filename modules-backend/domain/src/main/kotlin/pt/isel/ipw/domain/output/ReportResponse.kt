package pt.isel.ipw.domain.output

data class ReportResponse(
    val id: Int,
    val processId: Int,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
)

