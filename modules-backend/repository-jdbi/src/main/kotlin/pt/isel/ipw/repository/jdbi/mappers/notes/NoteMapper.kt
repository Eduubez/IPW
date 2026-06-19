package pt.isel.ipw.repository.jdbi.mappers.notes

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.ipw.domain.notes.Note
import java.sql.ResultSet

class NoteMapper(private val prefix: String = "") : RowMapper<Note> {
    override fun map(rs: ResultSet, ctx: StatementContext): Note {
        return Note(
            id = rs.getInt("${prefix}id"),
            processId = if (rs.getObject("${prefix}process_id") != null) rs.getInt("${prefix}process_id") else null,
            provesId = if (rs.getObject("${prefix}proves_id") != null) rs.getInt("${prefix}proves_id") else null,
            content = rs.getString("${prefix}content"),
            authorId = rs.getInt("${prefix}author_id"),
            creationDate = rs.getTimestamp("${prefix}created_at").toLocalDateTime(),
        )
    }
}
