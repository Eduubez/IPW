package pt.isel.ipw.domain.DTO.output.prove

data class ProveResponse(
    val id: Int,
    val processId: Int,
    val fileName: String,
    val contentType: String,
    val fileSize: Long,
    val createdBy: Int,
    val createdAt: String
)