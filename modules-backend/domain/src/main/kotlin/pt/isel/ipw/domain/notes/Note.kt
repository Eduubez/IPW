package pt.isel.ipw.domain.notes

import java.time.LocalDateTime

data class Note (
    val id: Int,
    val content: String,
    val authorId: Int,
    val creationDate: LocalDateTime
)