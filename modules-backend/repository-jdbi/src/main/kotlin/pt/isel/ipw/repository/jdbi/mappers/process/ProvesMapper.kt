package pt.isel.ipw.repository.jdbi.mappers.process

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.process.Prove
import java.sql.ResultSet


class ProvesMapper(private val prefix: String = "") : RowMapper<Prove> {
    override fun map(rs: ResultSet, ctx: StatementContext): Prove {
        return Prove(
            id = rs.getInt("${prefix}id"),
            processId = rs.getInt("${prefix}process_id"),
            fileName = rs.getString("${prefix}file_name"),
            fileType = rs.getString("${prefix}file_type"),
            fileUrl = rs.getString("${prefix}file_url"),
            createdAt = rs.getTimestamp("${prefix}created_at").toLocalDateTime(),
        )
    }
}