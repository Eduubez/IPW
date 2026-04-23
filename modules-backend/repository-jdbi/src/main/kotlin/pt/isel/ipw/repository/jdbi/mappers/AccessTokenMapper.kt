package pt.isel.ipw.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.AccessToken
import java.sql.ResultSet

class AccessTokenMapper : RowMapper<AccessToken> {
    override fun map(rs: ResultSet, ctx: StatementContext): AccessToken =
        AccessToken(
            token = rs.getString("token"),
            userId = rs.getInt("user_id"),
            role = rs.getString("role"),
            createdAt = rs.getTimestamp("created_at").toInstant(),
            expiresAt = rs.getTimestamp("expires_at").toInstant()
        )
}