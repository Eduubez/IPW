package pt.isel.ipw.services.interfaces

import pt.isel.ipw.domain.DTO.input.CreateNoteRequest
import pt.isel.ipw.services.results.CreateNoteResult
import pt.isel.ipw.services.results.GetNotesResult
import pt.isel.ipw.services.results.UpdateNoteResult

interface NoteService {

    fun createNote(processId: Int, note: CreateNoteRequest, userId: Int, role: String): CreateNoteResult

    fun getNotesByProveId(proveId:Int ,processId: Int, userId: Int, role: String): GetNotesResult

    fun getNotesByProcessId(processId: Int, userId: Int, role: String): GetNotesResult

    fun updateNote(noteId: Int, processId: Int, content: String, userId: Int): UpdateNoteResult
}