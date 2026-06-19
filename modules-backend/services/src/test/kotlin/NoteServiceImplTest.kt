import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ipw.domain.DTO.input.CreateNoteRequest
import pt.isel.ipw.domain.notes.Note
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.repository.jdbi.configureWithAppRequirements
import pt.isel.ipw.repository.jdbi.transaction.JdbiTransactionManager
import pt.isel.ipw.services.ActivityServiceImpl
import pt.isel.ipw.services.NoteServiceImpl
import pt.isel.ipw.services.ProcessServiceImpl
import pt.isel.ipw.services.ReportServiceImpl
import pt.isel.ipw.services.errors.Failure
import pt.isel.ipw.services.errors.NoteError
import pt.isel.ipw.services.errors.Success
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NoteServiceImplTest {

    companion object {
        val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setUrl("jdbc:postgresql://localhost:5433/postgres")
                user = "postgres"
                password = "changeit"
            }
        ).configureWithAppRequirements()

        private val trxManager = JdbiTransactionManager(jdbi)

        private val activityService = ActivityServiceImpl(trxManager)

        private val processService = ProcessServiceImpl(
            trxManager,
            activityService
        )

        private val noteService = NoteServiceImpl(
            trxManager,
            activityService
        )

        // Test users in sample_data
         private const val TRIATOR_ID = 1
        private const val INVESTIGATOR_ID = 2
        private const val SUPERVISOR_ID = 3
        private const val MANAGER_ID = 4
    }

    @BeforeTest
    fun cleanUp() {
        jdbi.useHandle<Exception> { handle ->
            handle.execute("call public.sample_data()")
        }
    }

    private fun createProcess(
        investigatorId: Int? = INVESTIGATOR_ID,
        supervisorId: Int? = SUPERVISOR_ID,
        area: String = "Car Accident"
    ): Int {
        val res = processService.createProcess(
            userId = TRIATOR_ID,
            name = "Test Process",
            street = "Augusta 1",
            county = "Lisbon",
            district = "Lisbon",
            latitude = null,
            longitude = null,
            area = area,
            priority = "normal",
            expiresAt = "2027-12-31T23:59:59",
            investigatorId = investigatorId,
            supervisorId = supervisorId,
            insuranceId = null,
            typificationId = null,
            canBeFraud = false,
            note = null
        )
        assertTrue(res is Success)
        return (res as Success).value
    }

    private fun createProve(processId: Int): Int =
        trxManager.run {
            provesRepository.create(
                processId = processId,
                fileName = "photo.jpg",
                contentType = "image/jpeg",
                fileSize = 1024L,
                storageKey = "key",
                createdBy = INVESTIGATOR_ID
            )
        }

    private fun createNoteOnProcess(processId: Int, authorId: Int = INVESTIGATOR_ID): Int {
        val req = CreateNoteRequest(processId = processId, provesId = null, content = "A note on the process")
        val res = noteService.createNote(processId, req, authorId, Roles.INVESTIGATOR)
        assertTrue(res is Success)
        return (res as Success).value
    }

    private fun createNoteOnProve(proveId: Int, processId: Int, authorId: Int = INVESTIGATOR_ID): Int {
        val req = CreateNoteRequest(processId = null, provesId = proveId, content = "Note for prove")
        val res = noteService.createNote(processId, req, authorId, Roles.INVESTIGATOR)
        assertTrue(res is Success)
        return (res as Success).value
    }

    // ------------------------
    // Create note on process
    // ------------------------

    @Test
    fun `createNote - success on process`() {
        val processId = createProcess()
        val result = noteService.createNote(processId, CreateNoteRequest(processId = processId, provesId = null, content = "Valid content"), INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Success)
        assertTrue((result as Success).value > 0)
    }

    @Test
    fun `createNote - empty content returns InvalidContent`() {
        val processId = createProcess()
        val result = noteService.createNote(processId, CreateNoteRequest(processId = processId, provesId = null, content = ""), INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(NoteError.InvalidContent, (result as Failure).value)
    }

    @Test
    fun `createNote - invalid note request both ids null returns InvalidNoteRequest`() {
        val processId = createProcess()
        val result = noteService.createNote(processId, CreateNoteRequest(processId = null, provesId = null, content = "Some content"), INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(NoteError.InvalidNoteRequest, (result as Failure).value)
    }

    @Test
    fun `createNote - invalid note request both ids present returns InvalidNoteRequest`() {
        val processId = createProcess()
        val proveId = createProve(processId)
        val result = noteService.createNote(processId, CreateNoteRequest(processId = processId, provesId = proveId, content = "Some content"), INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(NoteError.InvalidNoteRequest, (result as Failure).value)
    }

    @Test
    fun `createNote - process not found returns ProcessNotFound`() {
        val result = noteService.createNote(9999, CreateNoteRequest(processId = 9999, provesId = null, content = "Valid"), INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(NoteError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `createNote - triator not allowed returns UnauthorizedAccess`() {
        val processId = createProcess()
        val result = noteService.createNote(processId, CreateNoteRequest(processId = processId, provesId = null, content = "Valid"), TRIATOR_ID, Roles.TRIATOR)
        assertTrue(result is Failure)
        assertEquals(NoteError.UnauthorizedAccess, (result as Failure).value)
    }

    @Test
    fun `createNote - other investigator not assigned returns UnauthorizedAccess`() {
        val processId = createProcess(investigatorId = INVESTIGATOR_ID)
        val otherInvestigator = 8 // not assigned to this process in sample data
        val result = noteService.createNote(processId, CreateNoteRequest(processId = processId, provesId = null, content = "Valid"), otherInvestigator, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(NoteError.UnauthorizedAccess, (result as Failure).value)
    }

    // ------------------------
    // Create note on prove
    // ------------------------

    @Test
    fun `createNote - success on prove`() {
        val processId = createProcess()
        val proveId = createProve(processId)
        val result = noteService.createNote(processId, CreateNoteRequest(processId = null, provesId = proveId, content = "Valid content for prove"), INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Success)
        assertTrue((result as Success).value > 0)
    }

    @Test
    fun `createNote - invalid prove id returns ProveNotFound`() {
        val processId = createProcess()
        val result = noteService.createNote(processId, CreateNoteRequest(processId = null, provesId = 9999, content = "Valid"), INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(NoteError.ProveNotFound, (result as Failure).value)
    }

    // ------------------------
    // Get Notes by Process / Prove
    // ------------------------

    @Test
    fun `get notes by process id - success returns notes`() {
        val processId = createProcess()
        val noteId = createNoteOnProcess(processId)
        val res = noteService.getNotesByProcessId(processId, INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(res is Success)
        val notes = (res as Success).value
        assertTrue(notes.isNotEmpty())
        assertTrue(notes.any { it.content.contains("A note on the process") })
        val note = notes.first { it.id == noteId }
        assertEquals(INVESTIGATOR_ID, note.authorId)
    }

    @Test
    fun `get notes by prove id - success returns notes`() {
        val processId = createProcess()
        val proveId = createProve(processId)
        val noteId = createNoteOnProve(proveId, processId)
        val res = noteService.getNotesByProveId(proveId, processId, INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(res is Success)
        val notes = (res as Success).value
        assertTrue(notes.isNotEmpty())
        assertTrue(notes.any { it.id == noteId })
    }

    @Test
    fun `get notes by process id - unauthorized returns UnauthorizedAccess`() {
        val processId = createProcess()
        createNoteOnProcess(processId)
        val res = noteService.getNotesByProcessId(processId, TRIATOR_ID, Roles.TRIATOR)
        assertTrue(res is Failure)
        assertEquals(NoteError.UnauthorizedAccess, (res as Failure).value)
    }

    @Test
    fun `get notes by prove id - invalid prove id returns ProveNotFound`() {
        val processId = createProcess()
        val res = noteService.getNotesByProveId(123321, processId, INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(res is Failure)
        assertEquals(NoteError.ProveNotFound, (res as Failure).value)
    }
    // ------------------------
    // Update Note
    // ------------------------

    @Test
    fun `updateNote - success by author`() {
        val processId = createProcess()
        val noteId = createNoteOnProcess(processId, INVESTIGATOR_ID)

        val result = noteService.updateNote(noteId,processId, "Updated content for the note", INVESTIGATOR_ID)

        assertTrue(result is Success)

        // Verify the note was updated
        val notes = noteService.getNotesByProcessId(processId, INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(notes is Success)
        val updatedNote = (notes as Success).value.find { it.id == noteId }!!
        assertNotNull(updatedNote)
        assertEquals("Updated content for the note", updatedNote.content)
    }

    @Test
    fun `updateNote - note not found returns NoteNotFound`() {
        val result = noteService.updateNote(9999, 1, "Updated content", INVESTIGATOR_ID)

        assertTrue(result is Failure)
        assertEquals(NoteError.NoteNotFound, (result as Failure).value)
    }

    @Test
    fun `updateNote - not author returns UnauthorizedAccess`() {
        val processId = createProcess()
        val noteId = createNoteOnProcess(processId, INVESTIGATOR_ID)

        // Try to update with different user
        val result = noteService.updateNote(noteId, 1,"Hacked content", SUPERVISOR_ID)

        assertTrue(result is Failure)
        assertEquals(NoteError.UnauthorizedAccess, (result as Failure).value)
    }

    @Test
    fun `updateNote - invalid content empty returns InvalidContent`() {
        val processId = createProcess()
        val noteId = createNoteOnProcess(processId, INVESTIGATOR_ID)

        val result = noteService.updateNote(noteId, 1,"", INVESTIGATOR_ID)

        assertTrue(result is Failure)
        assertEquals(NoteError.InvalidContent, (result as Failure).value)
    }

    @Test
    fun `updateNote - invalid content too short returns InvalidContent`() {
        val processId = createProcess()
        val noteId = createNoteOnProcess(processId, INVESTIGATOR_ID)

        val result = noteService.updateNote(noteId,1, "ab", INVESTIGATOR_ID)

        assertTrue(result is Failure)
        assertEquals(NoteError.InvalidContent, (result as Failure).value)
    }

    @Test
    fun `updateNote - on prove by author success`() {
        val processId = createProcess()
        val proveId = createProve(processId)
        val noteId = createNoteOnProve(proveId, processId, INVESTIGATOR_ID)

        val result = noteService.updateNote(noteId, processId,"Updated prove note content", INVESTIGATOR_ID)

        assertTrue(result is Success)

        // Verify the note was updated
        val notes = noteService.getNotesByProveId(proveId, processId, INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(notes is Success)
        val updatedNote = (notes as Success).value.find { it.id == noteId }!!
        assertNotNull(updatedNote)
        assertEquals("Updated prove note content", updatedNote!!.content)
    }

}
