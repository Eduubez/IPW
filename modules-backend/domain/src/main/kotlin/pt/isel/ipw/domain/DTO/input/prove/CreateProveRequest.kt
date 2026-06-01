package pt.isel.ipw.domain.DTO.input.prove

data class CreateProveRequest(
    val fileName: String,
    val contentType: String,
    val fileSize: Long,
    val storageKey: String,
) {
}