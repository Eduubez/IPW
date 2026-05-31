package pt.isel.ipw.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.Activity
import java.sql.ResultSet

class ActivityMapper(private val prefix: String = "") : RowMapper<Activity> {
    override fun map(rs: ResultSet, ctx: StatementContext): Activity =
        Activity(
            id = rs.getInt("${prefix}id"),
            processId = rs.getInt("${prefix}process_id"),
            userId = rs.getInt("${prefix}user_id"),
            userName = rs.getString("${prefix}user_name"),
            action = rs.getString("${prefix}action"),
            description = rs.getString("${prefix}description"),
            createdAt = rs.getTimestamp("${prefix}created_at").toLocalDateTime()
        )
}
