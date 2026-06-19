package pt.isel.ipw.http.errors

import pt.isel.ipw.services.errors.NoteError

private val noteErrorMap = mapOf(
    NoteError.ProcessNotFound to Problem.processNotFound,
    NoteError.UnauthorizedAccess to Problem.unauthorized,
    NoteError.ProveNotFound to Problem.proveNotFound,
    NoteError.InvalidNoteRequest to Problem.invalidNoteRequest,
    NoteError.InvalidNoteRequest to Problem.invalidContent,
    NoteError.InvalidContent to Problem.invalidContent,
    NoteError.NoteNotFound to Problem.noteNotFound
)

fun NoteError.toHttp(): Pair<Int, Problem> =
    this.status to (noteErrorMap[this] ?: Problem.internalServerError)
