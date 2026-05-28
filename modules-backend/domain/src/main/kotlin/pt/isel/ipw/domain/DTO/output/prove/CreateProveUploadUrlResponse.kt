package pt.isel.ipw.domain.DTO.output.prove

data class CreateProveUploadUrlResponse(
    val uploadUrl: String,
    val storageKey: String,
) {
}