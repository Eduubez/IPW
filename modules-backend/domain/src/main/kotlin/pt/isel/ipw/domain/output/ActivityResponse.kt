package pt.isel.ipw.domain.output

data class ActivityResponse(
    val id: Int,
    val processId: Int,
    val userId: Int,
    val action: String,
    val description: String,
    val createdAt: String,
)

//fun Activity.toResponse() = ActivityResponse()

//fun List<Activity>.toResponse() = this.map { it.toResponse() }

