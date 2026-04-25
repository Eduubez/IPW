package pt.isel.ipw.domain.DTO.output


data class GetProcessResponse(
    val id: Int,
    val name: String,
    val location: LocationResponse,
    val creationDate: String,
    val dueDate: String,
    val priority: Int,
    val area: String,
    val typification: String,
    val triator: CreateUserResponse,
    val investigator: CreateUserResponse,
    val supervisor: CreateUserResponse,
    val state: String,
    val proves: ProvesResponse,
    val report: ReportResponse,
    val notes: NotesResponse,
    val activity: ActivityResponse,
    )


/*

fun Process.toResponse(): ProcessOutputModel = GetProcessResponse()

fun List<Process>.toResponse(): List<ProcessOutputModel> = this.map { it.toResponse() }

*/
