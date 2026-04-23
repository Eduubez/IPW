package pt.isel.ipw.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.LoginToken
import java.sql.ResultSet

class LoginTokenMapper : RowMapper<LoginToken> {
    override fun map(rs: ResultSet, ctx: StatementContext): LoginToken =
        LoginToken(
            token = rs.getString("token"),
            userId = rs.getInt("user_id"),
            createdAt = rs.getTimestamp("created_at").toInstant(),
            expiresAt = rs.getTimestamp("expires_at").toInstant()
        )

}