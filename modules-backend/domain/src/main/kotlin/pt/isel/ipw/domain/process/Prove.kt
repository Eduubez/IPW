package pt.isel.ipw.domain.process

import java.time.LocalDateTime

data class Prove(
    val id: Int,
    val processId: Int,
    val fileName: String,
    val contentType: String,
    val fileSize: Long,
    val storageKey: String,
    val createdBy: Int,
    val createdAt: LocalDateTime
)