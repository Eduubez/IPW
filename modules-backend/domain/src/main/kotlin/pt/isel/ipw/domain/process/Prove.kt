package pt.isel.ipw.domain.process

import java.time.LocalDateTime

data class Prove(
    val id: Int,
    val processId: Int,
    val fileName: String,
    val fileType: String,
    val fileUrl: String,
    val createdAt: LocalDateTime,
)