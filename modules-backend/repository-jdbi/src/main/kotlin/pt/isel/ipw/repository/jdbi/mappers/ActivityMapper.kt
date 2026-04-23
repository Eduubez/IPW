package pt.isel.ipw.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.Activity
import java.sql.ResultSet

class ActivityMapper : RowMapper<Activity> {
    override fun map(rs: ResultSet, ctx: StatementContext): Activity =
        Activity(
            id = rs.getInt("id"),
            processId = rs.getInt("process_id"),
            userId = rs.getInt("user_id"),
            action = rs.getString("action"),
            description = rs.getString("description"),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime()
        )
}