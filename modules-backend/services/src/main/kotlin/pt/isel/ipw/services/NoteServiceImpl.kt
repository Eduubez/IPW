package pt.isel.ipw.services

import org.springframework.stereotype.Service
import pt.isel.ipw.domain.ActivityActions
import pt.isel.ipw.domain.DTO.input.CreateNoteRequest
import pt.isel.ipw.domain.mapToString
import pt.isel.ipw.domain.process.AssignmentStateRole
import pt.isel.ipw.domain.process.ProcessView
import pt.isel.ipw.domain.process.State
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.domain.user.User
import pt.isel.ipw.repository.Transaction
import pt.isel.ipw.repository.TransactionManager
import pt.isel.ipw.services.errors.NoteError
import pt.isel.ipw.services.errors.failure
import pt.isel.ipw.services.errors.success
import pt.isel.ipw.services.interfaces.NoteService
import pt.isel.ipw.services.results.CreateNoteResult
import pt.isel.ipw.services.results.GetNotesResult
import pt.isel.ipw.services.results.UpdateNoteResult

@Service
class NoteServiceImpl(
    private val transactionManager: TransactionManager,
    private val activityServiceImpl: ActivityServiceImpl,
) : NoteService {


    override fun createNote(processId: Int, note: CreateNoteRequest, userId: Int, role: String): CreateNoteResult {
        return transactionManager.run {
            val process = processRepository.getById(processId)
                ?: return@run failure(NoteError.ProcessNotFound)

            if (process.state in setOf(
                    State.CANCELED,
                    State.APPROVED_BY_MANAGER
                )
            ) return@run failure(NoteError.ProcessFinished)

            if(!isPossibleToAddNote(process, role)){
                return@run failure(NoteError.UnauthorizedAccess)
            }

            val error = validateNote(
                note = note,
                process = process,
                proveId = note.proveId,
                userId = userId,
                role = role
            )

            error?.let { return@run failure(it) }

            val noteId = noteRepository.createNote(
                processId = note.processId,
                provesId = note.proveId,
                content = note.content,
                authorId = userId
            )

            activityServiceImpl.createActivity(
                processId = processId,
                userId = userId ,
                action = ActivityActions.CREATED_NOTE.mapToString(),
                description = "A new note was created for the process.",
            )

            success(noteId)
        }
    }

    override fun getNotesByProcessId(processId: Int, userId: Int, role: String): GetNotesResult =
        transactionManager.run {
            val process = processRepository.getById(processId)
                ?: return@run failure(NoteError.ProcessNotFound)

            if (!canAddNoteToProcess(process, userId, role)) {
                return@run failure(NoteError.UnauthorizedAccess)
            }

            val notes = noteRepository.getByProcessId(processId)
            success(notes)
        }

    override fun updateNote(noteId: Int, processId: Int, content: String, userId: Int): UpdateNoteResult =
        transactionManager.run {
            val process =
                processRepository.getById(processId)
                    ?: return@run failure(NoteError.ProcessNotFound)

            if (process.state in setOf(
                    State.CANCELED,
                    State.APPROVED_BY_MANAGER
                )
            )  return@run failure(NoteError.ProcessFinished)


            val note = noteRepository.getById(noteId)
                ?: return@run failure(NoteError.NoteNotFound)

            if (note.authorId != userId) {
                return@run failure(NoteError.UnauthorizedAccess)
            }

            if (!isValidContent(content)) {
                return@run failure(NoteError.InvalidContent)
            }


            noteRepository.updateNote(noteId, content)

            activityServiceImpl.createActivity(
                processId = process.id,
                userId = userId,
                action = ActivityActions.UPDATED_NOTE.mapToString(),
                description = "Note was updated.",
            )

            return@run success(Unit)
        }


    override fun getNotesByProveId(proveId: Int, processId: Int, userId: Int, role: String): GetNotesResult =
        transactionManager.run {

            val prove = provesRepository.getById(proveId) ?: return@run failure(NoteError.ProveNotFound)

            if (prove.processId != processId) {
                return@run failure(NoteError.ProcessNotFound)
            }
            val process = processRepository.getById(processId) ?: return@run failure(NoteError.ProcessNotFound)

            if (!canAddNoteToProcess(process, userId, role)) {
                return@run failure(NoteError.UnauthorizedAccess)
            }

            val notes = noteRepository.getByProveId(proveId)
            success(notes)
        }

    private fun Transaction.validateNote(
        note: CreateNoteRequest,
        process: ProcessView,
        proveId: Int?,
        userId: Int,
        role: String
    ): NoteError? =
        when {
            !isValidNoteRequest(note) -> NoteError.InvalidNoteRequest

            !canAddNoteToProcess(process, userId, role) -> NoteError.UnauthorizedAccess

            !isValidContent(note.content) -> NoteError.InvalidContent

            !isValidProveId(proveId) -> NoteError.ProveNotFound

            else -> null
        }


    private fun isValidNoteRequest(note: CreateNoteRequest): Boolean {
        val hasProcessId = note.processId != null
        val hasProvesId = note.proveId != null

        // XOR: exactly one must be true
        return (hasProcessId && !hasProvesId) || (!hasProcessId && hasProvesId)
    }


    private fun canAddNoteToProcess(
        process: ProcessView,
        userId: Int,
        role: String
    ): Boolean =
        process.investigator?.id == userId ||
                process.supervisor?.id == userId ||
                role == Roles.MANAGER


    private fun isValidContent(content: String): Boolean =
        content.isNotBlank() && content.length >= 3


    private fun Transaction.isValidProveId(proveId: Int?): Boolean {
        if (proveId == null) return true

        provesRepository.getById(proveId) ?: return false
        return true
    }

    private fun isPossibleToAddNote(process:ProcessView, role:String):Boolean {
        val targetStates = AssignmentStateRole.getStates(role)
        return process.state in targetStates
     }

}