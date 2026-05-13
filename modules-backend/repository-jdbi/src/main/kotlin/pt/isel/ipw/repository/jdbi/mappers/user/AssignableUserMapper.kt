package pt.isel.ipw.repository.jdbi.mappers.user

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.DTO.output.user.AssignableUser
import java.sql.ResultSet

class AssignableUserMapper : RowMapper<AssignableUser> {
    override fun map(rs: ResultSet, ctx: StatementContext): AssignableUser =
        AssignableUser(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            areaId = rs.getInt("area_id"),
            area = rs.getString("area")
        )
}