package pt.isel.ipw.domain.DTO.output


data class NoteResponse (
    val id: Int,
    val processId: Int?,
    val provesId: Int?,
    val content: String,
    val authorId: Int,
    val createdAt: String,
    )

