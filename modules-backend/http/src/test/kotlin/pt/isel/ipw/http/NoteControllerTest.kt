package pt.isel.ipw.http

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import pt.isel.ipw.domain.DTO.input.CreateNoteRequest
import pt.isel.ipw.domain.DTO.input.UpdateNoteRequest
import pt.isel.ipw.domain.DTO.output.CreateNoteResponse
import pt.isel.ipw.domain.DTO.output.ListResponse
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.http.controllers.NoteController
import pt.isel.ipw.http.errors.ErrorCode
import pt.isel.ipw.http.errors.Problem
import kotlin.test.Test

@SpringJUnitConfig(TestConfig::class)
class NoteControllerTest {

    @Autowired
    private lateinit var noteController: NoteController

    @AfterEach
    fun clearSecurityContext() {
        TestUtils.clearSecurityContext()
    }

    // ── createNote ────────────────────────────────────────────────────────────

    @Test
    fun createNote_success() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)
        val req = CreateNoteRequest(processId = 1, content = "Valid note content here")

        val resp = noteController.createNote(1, req)

        assertEquals(201, resp.statusCode.value())
        assertNotNull(resp.body as? CreateNoteResponse)
    }

    @Test
    fun createNote_processNotFound() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)
        val req = CreateNoteRequest(processId = 9999, content = "Valid note content here")

        val resp = noteController.createNote(9999, req)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/process-not-found", error.type)
        assertEquals(ErrorCode.PROCESS_NOT_FOUND, error.errorCode)
    }

    @Test
    fun createNote_invalidState_processNotAssigned() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)
        val req = CreateNoteRequest(processId = 6, content = "Valid note content here")

        val resp = noteController.createNote(6, req)
        val error = resp.body as Problem

        assertEquals(403, resp.statusCode.value())
        assertEquals("problems/unauthorized", error.type)
        assertEquals(ErrorCode.UNAUTHORIZED, error.errorCode)
    }

    @Test
    fun createNote_unauthorized_userNotAssignedToProcess() {
        TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.INVESTIGATOR)
        val req = CreateNoteRequest(processId = 1, content = "Valid note content here")

        val resp = noteController.createNote(1, req)
        val error = resp.body as Problem

        assertEquals(403, resp.statusCode.value())
        assertEquals("problems/unauthorized", error.type)
        assertEquals(ErrorCode.UNAUTHORIZED, error.errorCode)
    }

    @Test
    fun createNote_managerCannotCreateNote_processNotInManagerState() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)
        val req = CreateNoteRequest(processId = 1, content = "Valid note content here")

        val resp = noteController.createNote(1, req)
        val error = resp.body as Problem

        assertEquals(403, resp.statusCode.value())
        assertEquals("problems/unauthorized", error.type)
        assertEquals(ErrorCode.UNAUTHORIZED, error.errorCode)
    }

    @Test
    fun createNote_invalidContent_blank() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)
        val req = CreateNoteRequest(processId = 1, content = "   ")

        val resp = noteController.createNote(1, req)
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-content", error.type)
        assertEquals(ErrorCode.INVALID_CONTENT, error.errorCode)
    }

    @Test
    fun createNote_invalidContent_tooShort() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)
        val req = CreateNoteRequest(processId = 1, content = "ab")

        val resp = noteController.createNote(1, req)
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-content", error.type)
        assertEquals(ErrorCode.INVALID_CONTENT, error.errorCode)
    }

    @Test
    fun createNote_invalidNoteRequest_bothProcessIdAndProveIdSet() {
              TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)
        val req = CreateNoteRequest(processId = 1, proveId = 1, content = "Valid content here")

        val resp = noteController.createNote(1, req)
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-content", error.type)
        assertEquals(ErrorCode.INVALID_CONTENT, error.errorCode)
    }

    @Test
    fun createNote_invalidNoteRequest_neitherProcessIdNorProveId() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)
        val req = CreateNoteRequest(processId = null, proveId = null, content = "Valid content here")

        val resp = noteController.createNote(1, req)
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-content", error.type)
        assertEquals(ErrorCode.INVALID_CONTENT, error.errorCode)
    }

    // ── getNotesByProcessId ───────────────────────────────────────────────────

    @Test
    fun getNotesByProcessId_success_investigator() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = noteController.getNotesByProcessId(1)

        assertEquals(200, resp.statusCode.value())
        assertNotNull(resp.body as? ListResponse<*>)
    }

    @Test
    fun getNotesByProcessId_success_supervisor() {
        TestUtils.setUpSecurityContext(userId = TestUtils.SUPERVISOR_ID, role = Roles.SUPERVISOR)

        val resp = noteController.getNotesByProcessId(1)

        assertEquals(200, resp.statusCode.value())
    }

    @Test
    fun getNotesByProcessId_success_manager() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = noteController.getNotesByProcessId(1)

        assertEquals(200, resp.statusCode.value())
    }

    @Test
    fun getNotesByProcessId_processNotFound() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = noteController.getNotesByProcessId(9999)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/process-not-found", error.type)
        assertEquals(ErrorCode.PROCESS_NOT_FOUND, error.errorCode)
    }

    @Test
    fun getNotesByProcessId_unauthorized_userNotAssociatedToProcess() {
        TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.INVESTIGATOR)

        val resp = noteController.getNotesByProcessId(1)
        val error = resp.body as Problem

        assertEquals(403, resp.statusCode.value())
        assertEquals("problems/unauthorized", error.type)
        assertEquals(ErrorCode.UNAUTHORIZED, error.errorCode)
    }

    // ── getNotesByProveId ─────────────────────────────────────────────────────

    @Test
    fun getNotesByProveId_proveNotFound() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = noteController.getNotesByProveId(1, 9999)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/prove-not-found", error.type)
        assertEquals(ErrorCode.PROVE_NOT_FOUND, error.errorCode)
    }

    // ── updateNote ────────────────────────────────────────────────────────────

    @Test
    fun updateNote_noteNotFound() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = noteController.updateNote(1, 9999, UpdateNoteRequest(content = "Valid updated content"))
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/note-not-found", error.type)
        assertEquals(ErrorCode.NOTE_NOT_FOUND, error.errorCode)
    }

    @Test
    fun updateNote_success() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val createResp = noteController.createNote(1, CreateNoteRequest(processId = 1, content = "Note to be updated later"))
        assertEquals(201, createResp.statusCode.value())
        val noteId = (createResp.body as CreateNoteResponse).id

        val resp = noteController.updateNote(1, noteId, UpdateNoteRequest(content = "Successfully updated content"))

        assertEquals(204, resp.statusCode.value())
    }

    @Test
    fun updateNote_invalidContent_tooShort() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val createResp = noteController.createNote(1, CreateNoteRequest(processId = 1, content = "Note to be updated later"))
        assertEquals(201, createResp.statusCode.value())
        val noteId = (createResp.body as CreateNoteResponse).id

        val resp = noteController.updateNote(1, noteId, UpdateNoteRequest(content = "ab"))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-content", error.type)
        assertEquals(ErrorCode.INVALID_CONTENT, error.errorCode)
    }

    @Test
    fun updateNote_unauthorized_notTheAuthor() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)
        val createResp = noteController.createNote(1, CreateNoteRequest(processId = 1, content = "Note authored by investigator"))
        assertEquals(201, createResp.statusCode.value())
        val noteId = (createResp.body as CreateNoteResponse).id
        TestUtils.clearSecurityContext()

        TestUtils.setUpSecurityContext(userId = TestUtils.SUPERVISOR_ID, role = Roles.SUPERVISOR)
        val resp = noteController.updateNote(1, noteId, UpdateNoteRequest(content = "Trying to update someone else note"))
        val error = resp.body as Problem

        assertEquals(403, resp.statusCode.value())
        assertEquals("problems/unauthorized", error.type)
        assertEquals(ErrorCode.UNAUTHORIZED, error.errorCode)
    }
}
