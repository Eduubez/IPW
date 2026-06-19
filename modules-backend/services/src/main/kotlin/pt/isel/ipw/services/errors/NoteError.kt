package pt.isel.ipw.services.errors

sealed class NoteError(
    val status: Int
) {
    data object ProcessNotFound : NoteError(404)
    data object UnauthorizedAccess : NoteError(403)
    data object ProveNotFound : NoteError(404)
    data object InvalidNoteRequest : NoteError(400)
    data object InvalidContent : NoteError(400)
    data object NoteNotFound : NoteError(404)

}