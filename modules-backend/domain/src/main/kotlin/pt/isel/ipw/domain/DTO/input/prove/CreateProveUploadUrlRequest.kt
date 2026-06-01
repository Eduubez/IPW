package pt.isel.ipw.domain.DTO.input.prove

data class CreateProveUploadUrlRequest(
    val fileName: String,
    val contentType: String,
    val fileSize: Long
) {
}
