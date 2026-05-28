package pt.isel.ipw.domain.DTO.output.prove

data class ProveAccessUrlResponse(
    val url: String,
    val contentType: String,
    val fileName: String
)