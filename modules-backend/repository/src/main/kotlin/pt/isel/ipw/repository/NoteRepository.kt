package pt.isel.ipw.repository

import pt.isel.ipw.domain.notes.Note

interface NoteRepository {

    fun createNote(
        processId : Int?,
        provesId : Int?,
        content :String,
        authorId : Int
    ): Int

    fun getById(noteId: Int): Note?

    fun getByProcessId(processId: Int): List<Note>

    fun getByProveId(proveId: Int): List<Note>

    fun getAll(): List<Note>

    fun updateNote(noteId: Int, content: String): Unit

}