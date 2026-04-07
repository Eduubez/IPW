package pt.isel.ipw.domain.input

data class CreateNoteRequest (
    val processId:Int? = null,
    val provesId: Int? = null,
    val content: String,
)