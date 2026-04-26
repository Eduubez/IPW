package pt.isel.ipw.domain.DTO.output

data class ProvesResponse (
    val id: Int,
    val processId: Int,
    val fileName: String,
    val fileType: String,
    val fileUrl: String,
    val createdAt: String,
)


//fun Proves.toResponse: ProvesResponse = ProvesResponse()

//fun List<Proves>.toResponse(): List<ProvesResponse>
// = this.map { it.toResponse() }