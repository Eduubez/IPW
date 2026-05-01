package pt.isel.ipw.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.Entities.area.AreaView
import java.sql.ResultSet

class AreaViewMapper(private val prefix: String = "") : RowMapper<AreaView> {
    override fun map(rs: ResultSet, ctx: StatementContext): AreaView {
        return AreaView(
            id = rs.getInt("${prefix}id"),
            name = rs.getString("${prefix}name"),
            bossId = rs.getInt("${prefix}boss_id"),
            bossName = rs.getString("${prefix}boss_name")
        )
    }
}