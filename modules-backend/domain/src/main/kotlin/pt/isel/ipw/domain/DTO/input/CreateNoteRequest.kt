package pt.isel.ipw.domain.DTO.input

data class CreateNoteRequest (
    val processId:Int? = null,
    val provesId: Int? = null,
    val content: String,
)