package pt.isel.ipw.domain.notes

import pt.isel.ipw.domain.DTO.output.NoteResponse
import java.time.LocalDateTime

data class Note (
    val id: Int,
    val processId: Int?,
    val provesId: Int?,
    val content: String,
    val authorId: Int,
    val authorName: String,
    val creationDate: LocalDateTime
)

fun Note.toResponse(): NoteResponse =
    NoteResponse(
        id = id,
        processId = processId,
        provesId = provesId,
        content = content,
        authorId = authorId,
        authorName = authorName,
        createdAt = creationDate.toString(),
    )

fun List<Note>.toResponse(): List<NoteResponse> = this.map { it.toResponse() }