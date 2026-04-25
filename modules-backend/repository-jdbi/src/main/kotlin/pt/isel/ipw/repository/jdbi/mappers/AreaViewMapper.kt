package pt.isel.ipw.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.Entities.area.AreaView
import java.sql.ResultSet

class AreaViewMapper : RowMapper<AreaView> {
    override fun map(rs: ResultSet, ctx: StatementContext): AreaView {
        return AreaView(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            bossId = rs.getInt("boss_id"),
            bossName = rs.getString("boss_name")
        )
    }
}