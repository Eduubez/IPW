package pt.isel.ipw.domain.report

import java.time.LocalDateTime

class Report (
    val id: Int,
    val processId: Int,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)