package pt.isel.ipw.repository.jdbi.mappers.process

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.report.Report
import java.sql.ResultSet

class ReportMapper(private val prefix: String = "") : RowMapper<Report> {
    override fun map(rs: ResultSet, ctx: StatementContext): Report {
        return Report(
            id = rs.getInt("${prefix}id"),
            processId = rs.getInt("${prefix}process_id"),
            content = rs.getString("${prefix}content"),
            createdAt = rs.getTimestamp("${prefix}created_at").toLocalDateTime(),
            updatedAt = rs.getTimestamp("${prefix}updated_at").toLocalDateTime(),
        )
    }
}
