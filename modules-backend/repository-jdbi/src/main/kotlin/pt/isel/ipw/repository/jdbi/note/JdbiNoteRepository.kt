package pt.isel.ipw.repository.jdbi.note

import org.jdbi.v3.core.Handle
import pt.isel.ipw.domain.notes.Note
import pt.isel.ipw.repository.NoteRepository
import pt.isel.ipw.repository.jdbi.mappers.notes.NoteMapper

class JdbiNoteRepository(
    val handle: Handle
): NoteRepository{

    override fun createNote(
        processId: Int?,
        provesId: Int?,
        content: String,
        authorId: Int
    ): Int {
        return handle.createUpdate(
            """
            INSERT INTO Notes (process_id, proves_id, content, author_id)
            VALUES (:processId, :provesId, :content, :authorId)
            """
        )
            .bind("processId", processId)
            .bind("provesId", provesId)
            .bind("content", content)
            .bind("authorId", authorId)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()
    }

    override fun getById(noteId: Int): Note? =
        handle.createQuery(
            """
        select
            id,
            process_id,
            proves_id,
            content,
            author_id,
            created_at
        from Notes
        where id = :noteId
        """
        )
            .bind("noteId", noteId)
            .map(NoteMapper())
            .firstOrNull()



    override fun getByProcessId(processId: Int): List<Note> =
        handle.createQuery(
            """
            select
                n.id,
                n.process_id,
                n.proves_id,
                n.content,
                n.author_id,
                n.created_at,
                u.name as author_name
            from Notes n join Users u on n.id = u.id
            where process_id = :processId
            order by created_at desc
            """
        )
            .bind("processId", processId)
            .map(NoteMapper())
            .list()

    override fun getByProveId(proveId: Int): List<Note> =
        handle.createQuery(
            """
            select
                n.id,
                n.process_id,
                n.proves_id,
                n.content,
                n.author_id,
                n.created_at,
                u.name as author_name
            from Notes n join Users u on n.id = u.id
            where proves_id = :proveId
            order by created_at desc
            """
        )
            .bind("proveId", proveId)
            .map(NoteMapper())
            .list()

    override fun getAll(): List<Note> =
        handle.createQuery(
            """
            select
                n.id,
                n.process_id,
                n.proves_id,
                n.content,
                n.author_id,
                n.created_at,
                u.name as author_name
            from Notes n join Users u on n.id = u.id
            order by created_at desc
            """
        )
            .map(NoteMapper())
            .list()

    override fun updateNote(noteId: Int, content: String): Unit {
        handle.createUpdate(
            """
            UPDATE Notes
            SET content = :content
            WHERE id = :noteId
            """
        )
            .bind("noteId", noteId)
            .bind("content", content)
            .execute()
    }

}