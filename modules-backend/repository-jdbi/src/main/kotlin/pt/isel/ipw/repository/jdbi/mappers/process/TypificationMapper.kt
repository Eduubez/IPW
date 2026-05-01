package pt.isel.ipw.repository.jdbi.mappers.process

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.process.Typification
import java.sql.ResultSet

class TypificationMapper(private val prefix: String = "") : RowMapper<Typification> {
    override fun map(rs: ResultSet, ctx: StatementContext): Typification {
        return Typification(
            id = rs.getInt("${prefix}id"),
            name = rs.getString("${prefix}name"),
            honorary = rs.getLong("${prefix}honorary"),
        )
    }
}
