package pt.isel.ipw.domain.DTO.input


typealias UpdateReportRequest = CreateReportRequest

data class CreateReportRequest(
    val content: String,
) {
}