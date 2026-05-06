package pt.isel.ipw.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.Entities.area.AreaInfo
import java.sql.ResultSet

class AreaInfoMapper : RowMapper<AreaInfo> {
    override fun map(rs: ResultSet, ctx: StatementContext): AreaInfo =
        AreaInfo(
            areaId = rs.getInt("area_id"),
            area = rs.getString("area")
        )
}
