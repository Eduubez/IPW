package pt.isel.ipw.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.user.User
import java.sql.ResultSet

class UserMapper: RowMapper<User> {
    override fun map(
        rs: ResultSet,
        ctx: StatementContext
    ): User {
        return User(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            email = rs.getString("email"),
            passwordHash = rs.getString("password_hash"),
            area = rs.getString("area"),
            isActive = rs.getBoolean("is_active")
        )
    }
}