package pt.isel.ipw.domain.DTO.output

import pt.isel.ipw.domain.Activity

data class ActivityResponse(
    val id: Int,
    val processId: Int,
    val userId: Int,
    val action: String,
    val description: String,
    val createdAt: String,
)

fun Activity.toResponse() = ActivityResponse(
    id = id,
    processId = processId,
    userId = userId,
    action = action,
    description = description ?: "No description",
    createdAt = createdAt.toString()
)

fun List<Activity>.toResponse() = this.map { it.toResponse() }

