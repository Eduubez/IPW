package pt.isel.ipw.domain.input

data class UpdateNoteRequest (
    val id: Int,
    val newContent: String,
)