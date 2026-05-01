package pt.isel.ipw.domain.process

import pt.isel.ipw.domain.DTO.output.ActivityResponse
import pt.isel.ipw.domain.DTO.output.NotesResponse
import pt.isel.ipw.domain.Entities.area.AreaEntity
import pt.isel.ipw.domain.user.User
import pt.isel.ipw.domain.report.Report
import java.time.LocalDateTime

data class Process(
    val id: Int,
    val name: String,
    val location: Location,
    val creationDate: LocalDateTime,
    val dueDate: LocalDateTime,
    val priority: Priority,
    val area: AreaEntity,
    val typification: Typification,
    val triator: User,
    val investigator: User,
    val supervisor: User,
    val state: State,
    val proves: Proves,
    val report: Report,
    val notes: NotesResponse,
    val activity: ActivityResponse,
)

