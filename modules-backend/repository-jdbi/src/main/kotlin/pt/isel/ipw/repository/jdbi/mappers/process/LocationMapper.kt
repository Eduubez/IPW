package pt.isel.ipw.repository.jdbi.mappers.process

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.process.Location
import java.sql.ResultSet

class LocationMapper(private val prefix: String = "") : RowMapper<Location> {
    override fun map(rs: ResultSet, ctx: StatementContext): Location {
        return Location(
            id = rs.getInt("${prefix}id"),
            district = rs.getString("${prefix}district"),
            county = rs.getString("${prefix}county"),
            street = rs.getString("${prefix}street"),
            latitude = rs.getString("${prefix}latitude") ?: "",
            longitude = rs.getString("${prefix}longitude") ?: "",
        )
    }
}