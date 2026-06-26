import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import pt.isel.ipw.domain.DTO.input.CreateNoteRequest
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.repository.jdbi.transaction.JdbiTransactionManager
import pt.isel.ipw.services.ActivityServiceImpl
import pt.isel.ipw.services.NoteServiceImpl
import pt.isel.ipw.services.ProcessServiceImpl
import pt.isel.ipw.services.errors.Failure
import pt.isel.ipw.services.errors.NoteError
import pt.isel.ipw.services.errors.Success
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NoteServiceImplTest {

    companion object {
        val jdbi = DbConfig.getConnection()
        private val testUtils = TestUtils(jdbi)

        private val noteService = testUtils.noteService

    }

    @BeforeTest
    fun cleanUp() {
        testUtils.cleanRepo()
    }


    private fun createNoteOnProcess(processId: Int, authorId: Int = testUtils.INVESTIGATOR_ID): Int {
        val req = CreateNoteRequest(processId = processId, proveId = null, content = "A note on the process")
        val res = noteService.createNote(processId, req, authorId, Roles.INVESTIGATOR)
        assertTrue(res is Success)
        return (res as Success).value
    }

    private fun createNoteOnProve(proveId: Int, processId: Int, authorId: Int = testUtils.INVESTIGATOR_ID): Int {
        val req = CreateNoteRequest(processId = null, proveId = proveId, content = "Note for prove")
        val res = noteService.createNote(processId, req, authorId, Roles.INVESTIGATOR)
        assertTrue(res is Success)
        return (res as Success).value
    }

    // ------------------------
    // Create note on process
    // ------------------------

    @Test
    fun `createNote - success on process`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = noteService.createNote(
            processId,
            CreateNoteRequest(processId = processId, proveId = null, content = "Valid content"),
            testUtils.INVESTIGATOR_ID,
            Roles.INVESTIGATOR
        )
        assertTrue(result is Success)
        assertTrue((result as Success).value > 0)
    }

    @Test
    fun `createNote - empty content returns InvalidContent`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = noteService.createNote(
            processId,
            CreateNoteRequest(processId = processId, proveId = null, content = ""),
            testUtils.INVESTIGATOR_ID,
            Roles.INVESTIGATOR
        )
        assertTrue(result is Failure)
        assertEquals(NoteError.InvalidContent, (result as Failure).value)
    }

    @Test
    fun `createNote - invalid note request both ids null returns InvalidNoteRequest`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value


        val result = noteService.createNote(
            processId,
            CreateNoteRequest(processId = null, proveId = null, content = "Some content"),
            testUtils.INVESTIGATOR_ID,
            Roles.INVESTIGATOR
        )
        assertTrue(result is Failure)
        assertEquals(NoteError.InvalidNoteRequest, (result as Failure).value)
    }

    @Test
    fun `createNote - invalid note request both ids present returns InvalidNoteRequest`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val proveId = testUtils.createProve(processId)
        val result = noteService.createNote(
            processId,
            CreateNoteRequest(processId = processId, proveId = proveId, content = "Some content"),
            testUtils.INVESTIGATOR_ID,
            Roles.INVESTIGATOR
        )
        assertTrue(result is Failure)
        assertEquals(NoteError.InvalidNoteRequest, (result as Failure).value)
    }

    @Test
    fun `createNote - process not found returns ProcessNotFound`() {
        val result = noteService.createNote(
            9999,
            CreateNoteRequest(processId = 9999, proveId = null, content = "Valid"),
            testUtils.INVESTIGATOR_ID,
            Roles.INVESTIGATOR
        )
        assertTrue(result is Failure)
        assertEquals(NoteError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `createNote - triator not allowed returns UnauthorizedAccess`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = noteService.createNote(
            processId,
            CreateNoteRequest(processId = processId, proveId = null, content = "Valid"),
            testUtils.TRIATOR_ID,
            Roles.TRIATOR
        )
        assertTrue(result is Failure)
        assertEquals(NoteError.UnauthorizedAccess, (result as Failure).value)
    }

    @Test
    fun `createNote - other investigator not assigned returns UnauthorizedAccess`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val otherInvestigator = 8 // not assigned to this process in sample data
        val result = noteService.createNote(
            processId,
            CreateNoteRequest(processId = processId, proveId = null, content = "Valid"),
            otherInvestigator,
            Roles.INVESTIGATOR
        )
        assertTrue(result is Failure)
        assertEquals(NoteError.UnauthorizedAccess, (result as Failure).value)
    }


    @Test
    fun `createNote - try to add note when state is not related to role UnauthorizedAccess`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = noteService.createNote(
            processId,
            CreateNoteRequest(processId = processId, proveId = null, content = "Valid"),
            testUtils.SUPERVISOR_ID,
            Roles.SUPERVISOR
        )
        assertTrue(result is Failure)
        assertEquals(NoteError.UnauthorizedAccess, (result as Failure).value)
    }

    // ------------------------
    // Create note on prove
    // ------------------------

    @Test
    fun `createNote - success on prove`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val proveId = testUtils.createProve(processId)
        val result = noteService.createNote(
            processId,
            CreateNoteRequest(processId = null, proveId = proveId, content = "Valid content for prove"),
            testUtils.INVESTIGATOR_ID,
            Roles.INVESTIGATOR
        )
        assertTrue(result is Success)
        assertTrue((result as Success).value > 0)
    }

    @Test
    fun `createNote - invalid prove id returns ProveNotFound`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = noteService.createNote(
            processId,
            CreateNoteRequest(processId = null, proveId = 9999, content = "Valid"),
            testUtils.INVESTIGATOR_ID,
            Roles.INVESTIGATOR
        )
        assertTrue(result is Failure)
        assertEquals(NoteError.ProveNotFound, (result as Failure).value)
    }

    // ------------------------
    // Get Notes by Process / Prove
    // ------------------------

    @Test
    fun `get notes by process id - success returns notes`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val noteId = createNoteOnProcess(processId)
        val res = noteService.getNotesByProcessId(processId, testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(res is Success)
        val notes = (res as Success).value
        assertTrue(notes.isNotEmpty())
        assertTrue(notes.any { it.content.contains("A note on the process") })
        val note = notes.first { it.id == noteId }
        assertEquals(testUtils.INVESTIGATOR_ID, note.authorId)
    }

    @Test
    fun `get notes by prove id - success returns notes`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val proveId = testUtils.createProve(processId)
        val noteId = createNoteOnProve(proveId, processId)
        val res = noteService.getNotesByProveId(proveId, processId, testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(res is Success)
        val notes = (res as Success).value
        assertTrue(notes.isNotEmpty())
        assertTrue(notes.any { it.id == noteId })
    }

    @Test
    fun `get notes by process id - unauthorized returns UnauthorizedAccess`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        createNoteOnProcess(processId)
        val res = noteService.getNotesByProcessId(processId, testUtils.TRIATOR_ID, Roles.TRIATOR)
        assertTrue(res is Failure)
        assertEquals(NoteError.UnauthorizedAccess, (res as Failure).value)
    }

    @Test
    fun `get notes by prove id - invalid prove id returns ProveNotFound`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val res = noteService.getNotesByProveId(123321, processId, testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(res is Failure)
        assertEquals(NoteError.ProveNotFound, (res as Failure).value)
    }
    // ------------------------
    // Update Note
    // ------------------------

    @Test
    fun `updateNote - success by author`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val noteId = createNoteOnProcess(processId, testUtils.INVESTIGATOR_ID)

        val result =
            noteService.updateNote(noteId, processId, "Updated content for the note", testUtils.INVESTIGATOR_ID)

        assertTrue(result is Success)

        // Verify the note was updated
        val notes = noteService.getNotesByProcessId(processId, testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(notes is Success)
        val updatedNote = (notes as Success).value.find { it.id == noteId }!!
        assertNotNull(updatedNote)
        assertEquals("Updated content for the note", updatedNote.content)
    }

    @Test
    fun `updateNote - note not found returns NoteNotFound`() {
        val result = noteService.updateNote(9999, 1, "Updated content", testUtils.INVESTIGATOR_ID)

        assertTrue(result is Failure)
        assertEquals(NoteError.NoteNotFound, (result as Failure).value)
    }

    @Test
    fun `updateNote - not author returns UnauthorizedAccess`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val noteId = createNoteOnProcess(processId, testUtils.INVESTIGATOR_ID)

        // Try to update with different user
        val result = noteService.updateNote(noteId, 1, "Hacked content", testUtils.SUPERVISOR_ID)

        assertTrue(result is Failure)
        assertEquals(NoteError.UnauthorizedAccess, (result as Failure).value)
    }

    @Test
    fun `updateNote - invalid content empty returns InvalidContent`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val noteId = createNoteOnProcess(processId, testUtils.INVESTIGATOR_ID)

        val result = noteService.updateNote(noteId, 1, "", testUtils.INVESTIGATOR_ID)

        assertTrue(result is Failure)
        assertEquals(NoteError.InvalidContent, (result as Failure).value)
    }

    @Test
    fun `updateNote - invalid content too short returns InvalidContent`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val noteId = createNoteOnProcess(processId, testUtils.INVESTIGATOR_ID)

        val result = noteService.updateNote(noteId, 1, "ab", testUtils.INVESTIGATOR_ID)

        assertTrue(result is Failure)
        assertEquals(NoteError.InvalidContent, (result as Failure).value)
    }

    @Test
    fun `updateNote - on prove by author success`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val proveId = testUtils.createProve(processId)
        val noteId = createNoteOnProve(proveId, processId, testUtils.INVESTIGATOR_ID)

        val result = noteService.updateNote(noteId, processId, "Updated prove note content", testUtils.INVESTIGATOR_ID)

        assertTrue(result is Success)

        // Verify the note was updated
        val notes = noteService.getNotesByProveId(proveId, processId, testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(notes is Success)
        val updatedNote = (notes as Success).value.find { it.id == noteId }!!
        assertNotNull(updatedNote)
        assertEquals("Updated prove note content", updatedNote.content)
    }

}
