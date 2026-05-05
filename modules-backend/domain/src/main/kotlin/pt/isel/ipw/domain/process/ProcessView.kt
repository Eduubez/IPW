package pt.isel.ipw.domain.process

import pt.isel.ipw.domain.Activity
import pt.isel.ipw.domain.DTO.output.*
import pt.isel.ipw.domain.Entities.area.AreaView
import pt.isel.ipw.domain.notes.Note
import pt.isel.ipw.domain.report.Report
import pt.isel.ipw.domain.user.User
import java.time.LocalDateTime

// ProcessView
data class ProcessView(
    val id: Int,
    val name: String,
    val location: Location,
    val creationDate: LocalDateTime,
    val dueDate: LocalDateTime,
    val priority: Priority,
    val area: AreaView,
    val typification: Typification,
    val triator: User,
    val investigator: User?,
    val supervisor: User?,
    val state: State,
    val proves: Prove?,
    val report: Report?,
    val notes: List<Note>?,
    val activity: Activity?,
)



// VERIFICAR MAPEAMENTO DO USER
fun ProcessView.toResponse(): GetProcessResponse = GetProcessResponse(
    id = id,
    name = name,
    location = LocationResponse(
        id = location.id,
        district = location.district,
        county = location.county,
        street = location.street,
        latitude = location.latitude,
        longitude = location.longitude,
    ),
    creationDate = creationDate.toString(),
    dueDate = dueDate.toString(),
    priority = priority.name,
    area = area.name,
    typification = typification.name,
    triator = CreateUserResponse(
        id = triator.id,
        name = triator.name,
        email = triator.email,
        areaId = null,
        roles = emptyList()
    ),
    investigator = investigator?.let {
        CreateUserResponse(
        id = it.id,
        name = investigator.name,
        email = investigator.email,
        areaId = null,
        roles = emptyList()
    )
    },
    supervisor = supervisor?.let {
        CreateUserResponse(
        id = it.id,
        name = supervisor.name,
        email = supervisor.email,
        areaId = null,
        roles = emptyList()
    )
    },
    state = state.name.lowercase(),
    proves = proves?.let {
        ProvesResponse(
            id = it.id,
            processId = it.processId,
            fileName = it.fileName,
            fileType = it.fileType,
            fileUrl = it.fileUrl,
            createdAt = it.createdAt.toString(),
        )
    },
    report = report?.let {
        ReportResponse(
            id = it.id,
            processId = it.processId,
            content = it.content,
            createdAt = it.createdAt.toString(),
            updatedAt = it.updatedAt.toString(),
        )
    },
    notes = notes?.map { note ->
        NotesResponse(
            id = note.id,
            processId = note.processId,
            provesId = note.provesId,
            content = note.content,
            authorId = note.authorId,
            createdAt = note.creationDate.toString(),
        )
    },
    activity = activity?.toResponse()
)

fun List<ProcessView>.toResponse(): List<GetProcessResponse> = this.map { it.toResponse() }
