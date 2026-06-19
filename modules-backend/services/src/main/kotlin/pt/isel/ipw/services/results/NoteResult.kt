package pt.isel.ipw.services.results

import pt.isel.ipw.domain.notes.Note
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.NoteError

typealias CreateNoteResult = Either<NoteError, Int>

typealias GetNotesResult = Either<NoteError, List<Note>>

typealias UpdateNoteResult = Either<NoteError, Unit>