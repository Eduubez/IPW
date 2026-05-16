import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ipw.domain.process.State
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.repository.jdbi.configureWithAppRequirements
import pt.isel.ipw.repository.jdbi.transaction.JdbiTransactionManager
import pt.isel.ipw.services.ActivityServiceImpl
import pt.isel.ipw.services.ProcessServiceImpl
import pt.isel.ipw.services.ReportServiceImpl
import pt.isel.ipw.services.errors.Failure
import pt.isel.ipw.services.errors.ReportError
import pt.isel.ipw.services.errors.Success
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ReportServiceImplTest {

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

        private val processService = ProcessServiceImpl(trxManager, activityService)
        private val reportService = ReportServiceImpl(trxManager, activityService)

        // Alice(triator)=2, Bob(investigator)=3, Carol(supervisor)=4, Rafael(manager)=5
        private const val TRIATOR_ID = 2
        private const val INVESTIGATOR_ID = 3
        private const val SUPERVISOR_ID = 4
        private const val MANAGER_ID = 5
    }

    @BeforeTest
    fun cleanUp() {
        jdbi.useHandle<Exception> { handle ->
            handle.execute("call public.sample_data()")
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private fun createProcess(
        investigatorId: Int? = INVESTIGATOR_ID,
        supervisorId: Int? = SUPERVISOR_ID,
    ): Int {
        val result = processService.createProcess(
            userId = TRIATOR_ID,
            name = "Test Process",
            street = "Augusta Street 1",
            county = "Lisbon",
            district = "Lisbon",
            latitude = null,
            longitude = null,
            area = "Car Accident",
            priority = "normal",
            expiresAt = "2027-12-31T23:59:59",
            investigatorId = investigatorId,
            supervisorId = supervisorId,
            insuranceId = null,
            typificationId = null,
            canBeFraud = false,
            note = null,
        )
        assertTrue(result is Success)
        return (result as Success).value
    }

    private fun createReportAndGetId(processId: Int): Int =
        (reportService.createReport(processId, "Valid report content", INVESTIGATOR_ID) as Success).value

    // -----------------------------------------------------------------------
    // createReport
    // -----------------------------------------------------------------------

    @Test
    fun `createReport - success returns report id`() {
        val processId = createProcess()

        val result = reportService.createReport(processId, "Valid report content", INVESTIGATOR_ID)
        assertTrue(result is Success)
        assertTrue((result as Success).value > 0)
    }

    @Test
    fun `createReport - report id is positive`() {
        val processId = createProcess()

        val result = reportService.createReport(processId, "Detailed report", INVESTIGATOR_ID)
        assertTrue(result is Success)
        assertTrue((result as Success).value > 0)
    }

    // InvalidContent

    @Test
    fun `createReport - empty content returns InvalidContent`() {
        val processId = createProcess()

        val result = reportService.createReport(processId, "", INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.InvalidContent, (result as Failure).value)
    }

    @Test
    fun `createReport - blank content returns InvalidContent`() {
        val processId = createProcess()

        val result = reportService.createReport(processId, "   ", INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.InvalidContent, (result as Failure).value)
    }

    // ProcessNotFound

    @Test
    fun `createReport - process not found returns ProcessNotFound`() {
        val result = reportService.createReport(9999, "Valid content", INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `createReport - negative process id returns ProcessNotFound`() {
        val result = reportService.createReport(-1, "Valid content", INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessNotFound, (result as Failure).value)
    }

    // Unauthorized

    @Test
    fun `createReport - triator cannot create report returns Unauthorized`() {
        val processId = createProcess()
        val result = reportService.createReport(processId, "Valid content", TRIATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `createReport - supervisor cannot create report returns Unauthorized`() {
        val processId = createProcess()

        val result = reportService.createReport(processId, "Valid content", SUPERVISOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `createReport - manager cannot create report returns Unauthorized`() {
        val processId = createProcess()

        val result = reportService.createReport(processId, "Valid content", MANAGER_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `createReport - investigator not assigned to process returns Unauthorized`() {
        val processId = createProcess(investigatorId = INVESTIGATOR_ID)
        val otherInvestigatorId = 8

        val result = reportService.createReport(processId, "Valid content", otherInvestigatorId)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    // -----------------------------------------------------------------------
    // getByProcessId
    // -----------------------------------------------------------------------

    @Test
    fun `getByProcessId - success returns report`() {
        val processId = createProcess()
        reportService.createReport(processId, "Report content", INVESTIGATOR_ID)

        val result = reportService.getByProcessId(processId, INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Success)
        val report = (result as Success).value
        assertEquals(processId, report.processId)
        assertEquals("Report content", report.content)
        assertNotNull(report.createdAt)
        assertNotNull(report.updatedAt)
    }

    // ProcessNotFound

    @Test
    fun `getByProcessId - process not found returns ProcessNotFound`() {
        val result = reportService.getByProcessId(9999, INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessNotFound, (result as Failure).value)
    }

    // ReportNotFound

    @Test
    fun `getByProcessId - process exists but has no report returns ReportNotFound`() {
        val processId = createProcess()

        val result = reportService.getByProcessId(processId, INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.ReportNotFound, (result as Failure).value)
    }

    // Content validation

    @Test
    fun `getByProcessId - content matches what was created`() {
        val processId = createProcess()
        val content = "Detailed report about the incident"
        reportService.createReport(processId, content, INVESTIGATOR_ID)

        val result = reportService.getByProcessId(processId, INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Success)
        assertEquals(content, (result as Success).value.content)
    }

    // Role access

    @Test
    fun `getByProcessId - supervisor can read report`() {
        val processId = createProcess()
        reportService.createReport(processId, "Valid content", INVESTIGATOR_ID)

        val result = reportService.getByProcessId(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Success)
    }

    @Test
    fun `getByProcessId - manager can read report`() {
        val processId = createProcess()
        reportService.createReport(processId, "Valid content", INVESTIGATOR_ID)

        val result = reportService.getByProcessId(processId, MANAGER_ID, Roles.MANAGER)
        assertTrue(result is Success)
    }

    // -----------------------------------------------------------------------
    // updateReport
    // -----------------------------------------------------------------------

    @Test
    fun `updateReport - success updates content`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)

        val result = reportService.updateReport( processId, "Updated content", INVESTIGATOR_ID)
        assertTrue(result is Success)

        val updated = reportService.getByProcessId(processId, INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertEquals("Updated content", (updated as Success).value.content)
    }

    // InvalidContent

    @Test
    fun `updateReport - empty content returns InvalidContent`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)

        val result = reportService.updateReport(processId, "", INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.InvalidContent, (result as Failure).value)
    }

    @Test
    fun `updateReport - blank content returns InvalidContent`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)

        val result = reportService.updateReport(processId, "   ", INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.InvalidContent, (result as Failure).value)
    }

    // ProcessNotFound

    @Test
    fun `updateReport - process not found returns ProcessNotFound`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)

        val result = reportService.updateReport(9999, "Updated content", INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessNotFound, (result as Failure).value)
    }

    // Unauthorized

    @Test
    fun `updateReport - triator cannot update report returns Unauthorized`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)

        val result = reportService.updateReport(processId, "Updated content", TRIATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.UnauthorizedInvestigator, (result as Failure).value)
    }


    @Test
    fun `updateReport - other investigator cannot update report returns Unauthorized`() {
        val processId = createProcess(investigatorId = INVESTIGATOR_ID)
        val reportId = createReportAndGetId(processId)

        val result = reportService.updateReport(processId, "Updated content", 8)
        assertTrue(result is Failure)
        assertEquals(ReportError.UnauthorizedInvestigator, (result as Failure).value)
    }

    // -----------------------------------------------------------------------
    // deleteReport
    // -----------------------------------------------------------------------

    @Test
    fun `deleteReport - success deletes the report`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)
        val process = processService.getProcessById(processId,5,"manager")

        val result = reportService.deleteReport(processId, INVESTIGATOR_ID)
        assertTrue(result is Success)

        val fetched = reportService.getByProcessId(processId, INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(fetched is Failure)
        assertEquals(ReportError.ReportNotFound, (fetched as Failure).value)
    }

    // ProcessNotFound

    @Test
    fun `deleteReport - process not found returns ProcessNotFound`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)

        val result = reportService.deleteReport(9999, INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessNotFound, (result as Failure).value)
    }

    // Unauthorized

    @Test
    fun `deleteReport - triator cannot delete report returns Unauthorized`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)

        val result = reportService.deleteReport(processId, TRIATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `deleteReport - supervisor cannot delete report returns Unauthorized`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)

        val result = reportService.deleteReport(processId, SUPERVISOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `deleteReport - manager cannot delete report returns Unauthorized`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)

        val result = reportService.deleteReport(processId, MANAGER_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `deleteReport - other investigator cannot delete report returns Unauthorized`() {
        val processId = createProcess(investigatorId = INVESTIGATOR_ID)
        val reportId = createReportAndGetId(processId)

        val result = reportService.deleteReport(processId, 8)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

// -----------------------------------------------------------------------
// approveReport
// -----------------------------------------------------------------------

    @Test
    fun `approveReport - supervisor success transitions state to waiting_for_approval_manager`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)
        // State is already waiting_approval_supervisor

        val result = reportService.approveReport(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Success)

        val process = processService.getProcessById(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        assertEquals(State.WAITING_APPROVAL_MANAGER, (process as Success).value.state)
    }

    @Test
    fun `approveReport - manager success transitions state to approved_by_manager`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)
        reportService.approveReport(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        // State is now waiting_for_approval_manager

        val result = reportService.approveReport(processId, MANAGER_ID, Roles.MANAGER)
        assertTrue(result is Success)

        val process = processService.getProcessById(processId, MANAGER_ID, Roles.MANAGER)
        assertEquals(State.APPROVED_BY_MANAGER, (process as Success).value.state)
    }

// ProcessNotFound

    @Test
    fun `approveReport - process not found returns ProcessNotFound`() {
        val result = reportService.approveReport(9999, SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessNotFound, (result as Failure).value)
    }

// ReportNotFound

    @Test
    fun `approveReport - no report returns ReportNotFound`() {
        val processId = createProcess()
        // No report created — state is 'assigned'

        val result = reportService.approveReport(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.ReportNotFound, (result as Failure).value)
    }

// AlreadyApproved

    @Test
    fun `approveReport - manager tries approve twice returns AlreadyApproved`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)
        reportService.approveReport(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        reportService.approveReport(processId, MANAGER_ID, Roles.MANAGER)

        val result = reportService.approveReport(processId, MANAGER_ID, Roles.MANAGER)
        assertTrue(result is Failure)
        assertEquals(ReportError.AlreadyApproved, (result as Failure).value)
    }

    @Test
    fun `approveReport - supervisor tries approve state waiting for manager returns AlreadyApproved`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)
        reportService.approveReport(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        // State is now waiting_for_approval_manager

        val result = reportService.approveReport(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.AlreadyApproved, (result as Failure).value)
    }

// Unauthorized

    @Test
    fun `approveReport - investigator cannot approve returns Unauthorized`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)
        // State is already waiting_approval_supervisor

        val result = reportService.approveReport(processId, INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `approveReport - triator cannot approve returns Unauthorized`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)
        // State is already waiting_approval_supervisor

        val result = reportService.approveReport(processId, TRIATOR_ID, Roles.TRIATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }


// -----------------------------------------------------------------------
// rejectReport
// -----------------------------------------------------------------------

    @Test
    fun `rejectReport - supervisor success transitions state to rejected_by_supervisor`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)
        // State is already waiting_approval_supervisor

        val result = reportService.rejectReport(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Success)

        val process = processService.getProcessById(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        assertEquals(State.REJECTED_BY_SUPERVISOR, (process as Success).value.state)
    }

    @Test
    fun `rejectReport - manager success transitions state to rejected_by_manager`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)
        reportService.approveReport(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        // State is now waiting_for_approval_manager

        val result = reportService.rejectReport( processId, MANAGER_ID, Roles.MANAGER)
        assertTrue(result is Success)

        val process = processService.getProcessById(processId, MANAGER_ID, Roles.MANAGER)
        assertEquals(State.REJECTED_BY_MANAGER, (process as Success).value.state)
    }

// ProcessNotFound

    @Test
    fun `rejectReport - process not found returns ProcessNotFound`() {
        val result = reportService.rejectReport(9999, SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessNotFound, (result as Failure).value)
    }

// ReportNotFound

    @Test
    fun `rejectReport - no report returns ReportNotFound`() {
        val processId = createProcess()
        // No report created — state is 'assigned'

        val result = reportService.rejectReport(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.ReportNotFound, (result as Failure).value)
    }

// AlreadyRejected

    @Test
    fun `rejectReport - supervisor tries reject twice returns AlreadyRejected`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)
        reportService.rejectReport( processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        // State is now rejected_by_supervisor — not valid for reject

        val result = reportService.rejectReport(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.AlreadyRejected, (result as Failure).value)
    }

    @Test
    fun `rejectReport - supervisor tries reject state waiting for manager returns AlreadyApproved`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)
        reportService.approveReport(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        // State is now waiting_for_approval_manager

        val result = reportService.rejectReport(processId, SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.AlreadyApproved, (result as Failure).value)
    }

// Unauthorized

    @Test
    fun `rejectReport - investigator cannot reject returns Unauthorized`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)
        // State is already waiting_approval_supervisor

        val result = reportService.rejectReport(processId, INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `rejectReport - triator cannot reject returns Unauthorized`() {
        val processId = createProcess()
        val reportId = createReportAndGetId(processId)
        // State is already waiting_approval_supervisor

        val result = reportService.rejectReport(processId, TRIATOR_ID, Roles.TRIATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

}