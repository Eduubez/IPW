package pt.isel.ipw.repository.jdbi.mappers.user

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.user.UserWithRoles
import java.sql.ResultSet

class UserWithRolesMapper : RowMapper<UserWithRoles> {
    override fun map(
        rs: ResultSet,
        ctx: StatementContext
    ): UserWithRoles {
        val rolesArray = rs.getArray("roles")
        val roles = (rolesArray.array as Array<*>)
            .filterIsInstance<String>()

        return UserWithRoles(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            email = rs.getString("email"),
            areaId = rs.getInt("area_id").let { if (rs.wasNull()) null else it },
            area = rs.getString("area"),
            isActive = rs.getBoolean("is_active"),
            roles = roles
        )
    }
}