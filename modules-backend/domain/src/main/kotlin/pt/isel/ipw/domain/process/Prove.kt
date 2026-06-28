package pt.isel.ipw.domain.process

import pt.isel.ipw.domain.DTO.output.prove.ProveResponse
import java.time.LocalDateTime

data class Prove(
    val id: Int,
    val processId: Int,
    val fileName: String,
    val contentType: String,
    val fileSize: Long,
    val storageKey: String,
    val createdBy: Int,
    val authorName: String,
    val createdAt: LocalDateTime
)

fun Prove.toResponse() = ProveResponse(
    id = id,
    processId = processId,
    fileName = fileName,
    contentType = contentType,
    fileSize = fileSize,
    createdBy = createdBy,
    authorName = authorName,
    createdAt = createdAt.toString()
)


fun List<Prove>.toResponse() = map { it.toResponse() }
