package pt.isel.ipw.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.User
import java.sql.ResultSet

class UserMapper(private val prefix: String = "") : RowMapper<User> {
    override fun map(
        rs: ResultSet,
        ctx: StatementContext
    ): User {
        return User(
            id = rs.getInt("${prefix}id"),
            name = rs.getString("${prefix}name"),
            email = rs.getString("${prefix}email"),
            passwordHash = rs.getString("${prefix}password_hash"),
            area = rs.getString("${prefix}area"),
            isActive = rs.getBoolean("${prefix}is_active")
        )
    }
}