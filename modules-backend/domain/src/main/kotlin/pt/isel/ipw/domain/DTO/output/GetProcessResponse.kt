package pt.isel.ipw.domain.DTO.output

import pt.isel.ipw.domain.DTO.output.prove.ProveResponse


data class GetProcessResponse(
    val id: Int,
    val name: String,
    val location: LocationResponse,
    val creationDate: String,
    val dueDate: String,
    val priority: String,
    val area: String,
    val typification: String,
    val triator: CreateUserResponse,
    val investigator: CreateUserResponse?,
    val supervisor: CreateUserResponse?,
    val state: String,
    val proves: List<ProveResponse>?,
    val report: ReportResponse?,
    val notes: List<NoteResponse>?,
    val activity: List<ActivityResponse>?,
)


