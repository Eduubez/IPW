package pt.isel.ipw.repository.jdbi.mappers.process

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.Entities.area.AreaEntity
import pt.isel.ipw.domain.notes.Note
import pt.isel.ipw.domain.process.*
import pt.isel.ipw.repository.jdbi.mappers.ActivityMapper
import pt.isel.ipw.repository.jdbi.mappers.AreaViewMapper
import pt.isel.ipw.repository.jdbi.mappers.UserMapper
import java.sql.ResultSet
class ProcessMapper(private val notes: List<Note>) : RowMapper<ProcessView> {
    override fun map(rs: ResultSet, ctx: StatementContext): ProcessView =
        ProcessView(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            location = LocationMapper("location_").map(rs, ctx),
            creationDate = rs.getTimestamp("creation_date").toLocalDateTime(),
            dueDate = rs.getTimestamp("due_date").toLocalDateTime(),
            priority = rs.getString("priority").toPriority(),
            area = AreaViewMapper("area_").map(rs, ctx),
            typification = TypificationMapper("typification_").map(rs, ctx),
            triator = UserMapper("triator_").map(rs, ctx),
            investigator = if (rs.getObject("investigator_id") != null) UserMapper("investigator_").map(rs, ctx) else null,
            supervisor = if (rs.getObject("supervisor_id") != null) UserMapper("supervisor_").map(rs, ctx) else null,
            state = rs.getString("state_")?.toState() ?: State.NOT_ASSIGNED,
            proves = if (rs.getObject("proves_id") != null) ProvesMapper("proves_").map(rs, ctx) else null,
            report = if (rs.getObject("report_id") != null) ReportMapper("report_").map(rs, ctx) else null,
            notes = notes,
            activity = if (rs.getObject("activity_id") != null) ActivityMapper("activity_").map(rs, ctx) else null,
        )
}
