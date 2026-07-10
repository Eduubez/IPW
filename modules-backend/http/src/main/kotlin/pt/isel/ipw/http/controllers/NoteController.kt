package pt.isel.ipw.http.controllers

import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ipw.domain.DTO.input.CreateNoteRequest
import pt.isel.ipw.domain.DTO.input.UpdateNoteRequest
import pt.isel.ipw.domain.DTO.output.CreateNoteResponse
import pt.isel.ipw.domain.DTO.output.ListResponse
import pt.isel.ipw.domain.notes.toResponse
import pt.isel.ipw.domain.process.toResponse
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.http.auth.AuthenticatedUser
import pt.isel.ipw.http.errors.Problem
import pt.isel.ipw.http.errors.handler
import pt.isel.ipw.http.errors.toHttp
import pt.isel.ipw.services.errors.mapSuccess
import pt.isel.ipw.services.interfaces.NoteService

@RestController
@RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)
@RequestMapping("/api/process")
class NoteController(
    private val noteService: NoteService
) {

    @RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)

    @PostMapping("/{id}/note")
    fun createNote(
        @PathVariable id: Int,
        @RequestBody note: CreateNoteRequest,
    ): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidToken)


        val result = noteService.createNote(id, note, userId, role)
            .mapSuccess { CreateNoteResponse(id= it) }
        return handler(result, HttpStatus.CREATED) { error -> error.toHttp() }
    }

    @RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)
    @GetMapping("/{id}/note")
    fun getNotesByProcessId(
        @PathVariable id: Int,
    ): ResponseEntity<*> {

        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidToken)


        val result = noteService.getNotesByProcessId(id, userId, role)
            .mapSuccess { notes ->
                ListResponse(
                    results = notes.map { it.toResponse() }
                )
            }

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }


    @RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)

    @GetMapping("/{id}/proves/{proveId}/note")
    fun getNotesByProveId(
        @PathVariable id: Int,
        @PathVariable proveId: Int,
    ): ResponseEntity<*> {

        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidToken)


        val result = noteService.getNotesByProveId(proveId, id, userId, role)
            .mapSuccess { notes ->
                ListResponse(
                    results = notes.map { it.toResponse() }
                )
            }

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }


    @RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)
    @PatchMapping("/{id}/note/{noteId}")
    fun updateNote(
        @PathVariable id: Int,
        @PathVariable noteId: Int,
        @RequestBody note: UpdateNoteRequest
    ): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = noteService.updateNote(noteId, id, note.content, userId)

        return handler(result, HttpStatus.NO_CONTENT) { error -> error.toHttp() }
    }

}
