package pt.isel.ipw.domain.DTO.output


data class NotesResponse (
    val id: Int,
    val processId: Int?,
    val proves: Int?,
    val content: String,
    val authorId: Int,
    val createdAt: String,
    )

//fun Note.toResponse():GetNotesResponse = GetNotesResponse()

//fun List<Note>.toResponse():List<GetNoteReponse> = this.map { it.toResponse() }