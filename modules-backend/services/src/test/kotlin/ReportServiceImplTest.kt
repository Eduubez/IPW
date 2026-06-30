import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import pt.isel.ipw.domain.process.State
import pt.isel.ipw.domain.roles.Roles

import pt.isel.ipw.services.errors.Failure
import pt.isel.ipw.services.errors.ReportError
import pt.isel.ipw.services.errors.Success
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ReportServiceImplTest {

    companion object {
        val jdbi = DbConfig.getConnection()
        private val testUtils = TestUtils(jdbi)
        private val processService = testUtils.processService
        private val reportService = testUtils.reportService
    }

    @BeforeTest
    fun cleanUp() {
        testUtils.cleanRepo()
    }

    private fun createReportAndGetId(processId: Int): Int =
        (reportService.createReport(processId, "Valid report content", testUtils.INVESTIGATOR_ID) as Success).value

    // -----------------------------------------------------------------------
    // createReport
    // -----------------------------------------------------------------------

    @Test
    fun `createReport - success returns report id`() {
        val process = testUtils.createProcess()
        assertTrue(process is Success)
        val processId = (process as Success).value

        val result = reportService.createReport(processId, "Valid report content", testUtils.INVESTIGATOR_ID)
        assertTrue(result is Success)
        assertTrue((result as Success).value > 0)
    }

    @Test
    fun `createReport - report id is positive`() {
        val process = testUtils.createProcess()
        assertTrue(process is Success)
        val processId = (process as Success).value

        val result = reportService.createReport(processId, "Detailed report", testUtils.INVESTIGATOR_ID)
        assertTrue(result is Success)
        assertTrue((result as Success).value > 0)
    }

    // InvalidContent

    @Test
    fun `createReport - empty content returns InvalidContent`() {
        val process = testUtils.createProcess()
        assertTrue(process is Success)
        val processId = (process as Success).value

        val result = reportService.createReport(processId, "", testUtils.INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.InvalidContent, (result as Failure).value)
    }

    @Test
    fun `createReport - blank content returns InvalidContent`() {
        val process = testUtils.createProcess()
        assertTrue(process is Success)
        val processId = (process as Success).value

        val result = reportService.createReport(processId, "   ", testUtils.INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.InvalidContent, (result as Failure).value)
    }

    // ProcessNotFound

    @Test
    fun `createReport - process not found returns ProcessNotFound`() {
        val result = reportService.createReport(9999, "Valid content", testUtils.INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `createReport - negative process id returns ProcessNotFound`() {
        val result = reportService.createReport(-1, "Valid content", testUtils.INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessNotFound, (result as Failure).value)
    }

    // Unauthorized

    @Test
    fun `createReport - triator cannot create report returns Unauthorized`() {
        val process = testUtils.createProcess()
        assertTrue(process is Success)
        val processId = (process as Success).value

        val result = reportService.createReport(processId, "Valid content", testUtils.TRIATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `createReport - supervisor cannot create report returns Unauthorized`() {
        val process = testUtils.createProcess()
        assertTrue(process is Success)
        val processId = (process as Success).value

        val result = reportService.createReport(processId, "Valid content", testUtils.SUPERVISOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `createReport - manager cannot create report returns Unauthorized`() {
        val process = testUtils.createProcess()
        assertTrue(process is Success)
        val processId = (process as Success).value

        val result = reportService.createReport(processId, "Valid content", testUtils.MANAGER_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `createReport - investigator not assigned to process returns Unauthorized`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value
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
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value
        reportService.createReport(processId, "Report content", testUtils.INVESTIGATOR_ID)

        val result = reportService.getByProcessId(processId, testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
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
        val result = reportService.getByProcessId(9999, testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessNotFound, (result as Failure).value)
    }

    // ReportNotFound

    @Test
    fun `getByProcessId - process exists but has no report returns ReportNotFound`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val result = reportService.getByProcessId(processId, testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.ReportNotFound, (result as Failure).value)
    }

    // Content validation

    @Test
    fun `getByProcessId - content matches what was created`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value
        val content = "Detailed report about the incident"
        reportService.createReport(processId, content, testUtils.INVESTIGATOR_ID)

        val result = reportService.getByProcessId(processId, testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Success)
        assertEquals(content, (result as Success).value.content)
    }

    // Role access

    @Test
    fun `getByProcessId - supervisor can read report`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value
        reportService.createReport(processId, "Valid content", testUtils.INVESTIGATOR_ID)

        val result = reportService.getByProcessId(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Success)
    }

    @Test
    fun `getByProcessId - manager can read report`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value
        reportService.createReport(processId, "Valid content", testUtils.INVESTIGATOR_ID)

        val result = reportService.getByProcessId(processId, testUtils.MANAGER_ID, Roles.MANAGER)
        assertTrue(result is Success)
    }

    // -----------------------------------------------------------------------
    // updateReport
    // -----------------------------------------------------------------------

    @Test
    fun `updateReport - success updates content`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value
        val reportId = createReportAndGetId(processId)

        val result = reportService.updateReport(processId, "Updated content", testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Success)

        val updated = reportService.getByProcessId(processId, testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertEquals("Updated content", (updated as Success).value.content)
    }

    // InvalidContent

    @Test
    fun `updateReport - empty content returns InvalidContent`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value
        val reportId = createReportAndGetId(processId)

        val result = reportService.updateReport(processId, "", testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.InvalidContent, (result as Failure).value)
    }

    @Test
    fun `updateReport - blank content returns InvalidContent`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value
        val reportId = createReportAndGetId(processId)

        val result = reportService.updateReport(processId, "   ", testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.InvalidContent, (result as Failure).value)
    }

    // ProcessNotFound

    @Test
    fun `updateReport - process not found returns ProcessNotFound`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)

        val result = reportService.updateReport(9999, "Updated content", testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessNotFound, (result as Failure).value)
    }

    // Unauthorized

    @Test
    fun `updateReport - triator cannot update report returns Unauthorized`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)

        val result = reportService.updateReport(processId, "Updated content", testUtils.TRIATOR_ID, Roles.TRIATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.InvalidState, (result as Failure).value)
    }

    @Test
    fun `updateReport - other investigator cannot update report returns Unauthorized`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value
        val reportId = createReportAndGetId(processId)

        val result = reportService.updateReport(processId, "Updated content", 8, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.UnauthorizedInvestigator, (result as Failure).value)
    }

    // -----------------------------------------------------------------------
    // deleteReport
    // -----------------------------------------------------------------------

    @Test
    fun `deleteReport - success deletes the report`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value


        val reportId = createReportAndGetId(processId)
        val processB = processService.getProcessById(processId, 5, "manager")

        val result = reportService.deleteReport(processId, testUtils.INVESTIGATOR_ID)
        assertTrue(result is Success)

        val fetched = reportService.getByProcessId(processId, testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(fetched is Failure)
        assertEquals(ReportError.ReportNotFound, (fetched as Failure).value)
    }

    // ProcessNotFound

    @Test
    fun `deleteReport - process not found returns ProcessNotFound`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)

        val result = reportService.deleteReport(9999, testUtils.INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessNotFound, (result as Failure).value)
    }

    // Unauthorized

    @Test
    fun `deleteReport - triator cannot delete report returns Unauthorized`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)

        val result = reportService.deleteReport(processId, testUtils.TRIATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `deleteReport - supervisor cannot delete report returns Unauthorized`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)

        val result = reportService.deleteReport(processId, testUtils.SUPERVISOR_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `deleteReport - manager cannot delete report returns Unauthorized`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)

        val result = reportService.deleteReport(processId, testUtils.MANAGER_ID)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `deleteReport - other investigator cannot delete report returns Unauthorized`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

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
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)
        processService.submitProcess(testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR, processId)

        val result = reportService.approveReport(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Success)

        val processB = processService.getProcessById(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertEquals(State.WAITING_APPROVAL_MANAGER, (processB as Success).value.state)
    }

    @Test
    fun `approveReport - manager success transitions state to approved_by_manager`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)
        processService.submitProcess(testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR, processId)

        reportService.approveReport(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)

        val result = reportService.approveReport(processId, testUtils.MANAGER_ID, Roles.MANAGER)
        assertTrue(result is Success)

        val processB = processService.getProcessById(processId, testUtils.MANAGER_ID, Roles.MANAGER)
        assertEquals(State.APPROVED_BY_MANAGER, (processB as Success).value.state)
    }

    // ProcessNotFound

    @Test
    fun `approveReport - process not found returns ProcessNotFound`() {
        val result = reportService.approveReport(9999, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessNotFound, (result as Failure).value)
    }

    // ReportNotFound

    @Test
    fun `approveReport - no report returns ReportNotFound`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value


        val result = reportService.approveReport(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.ReportNotFound, (result as Failure).value)
    }

    // AlreadyApproved

    @Test
    fun `approveReport - manager tries approve twice returns ProcessFinished`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)
        processService.submitProcess(testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR, processId)

        reportService.approveReport(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        reportService.approveReport(processId, testUtils.MANAGER_ID, Roles.MANAGER)

        val result = reportService.approveReport(processId, testUtils.MANAGER_ID, Roles.MANAGER)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessFinished, (result as Failure).value)
    }

    @Test
    fun `approveReport - supervisor tries approve state waiting for manager returns AlreadyApproved`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)
        processService.submitProcess(testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR, processId)

        reportService.approveReport(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)

        val result = reportService.approveReport(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.AlreadyApproved, (result as Failure).value)
    }

    // Unauthorized

    @Test
    fun `approveReport - investigator cannot approve returns Unauthorized`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)

        val result = reportService.approveReport(processId, testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `approveReport - triator cannot approve returns Unauthorized`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)

        val result = reportService.approveReport(processId, testUtils.TRIATOR_ID, Roles.TRIATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    // -----------------------------------------------------------------------
    // rejectReport
    // -----------------------------------------------------------------------

    @Test
    fun `rejectReport - supervisor success transitions state to rejected_by_supervisor`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)
        processService.submitProcess(testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR, processId)

        val result = reportService.rejectReport(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Success)

        val processB = processService.getProcessById(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertEquals(State.REJECTED_BY_SUPERVISOR, (processB as Success).value.state)
    }

    @Test
    fun `rejectReport - manager success transitions state to rejected_by_manager`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)
        processService.submitProcess(testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR, processId)

        reportService.approveReport(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)

        val result = reportService.rejectReport(processId, testUtils.MANAGER_ID, Roles.MANAGER)
        assertTrue(result is Success)

        val processB = processService.getProcessById(processId, testUtils.MANAGER_ID, Roles.MANAGER)
        assertEquals(State.REJECTED_BY_MANAGER, (processB as Success).value.state)
    }

    // ProcessNotFound

    @Test
    fun `rejectReport - process not found returns ProcessNotFound`() {
        val result = reportService.rejectReport(9999, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.ProcessNotFound, (result as Failure).value)
    }

    // ReportNotFound

    @Test
    fun `rejectReport - no report returns ReportNotFound`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value


        val result = reportService.rejectReport(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.ReportNotFound, (result as Failure).value)
    }

    // AlreadyRejected

    @Test
    fun `rejectReport - supervisor tries reject twice returns AlreadyRejected`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)
        processService.submitProcess(testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR, processId)

        reportService.rejectReport(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)

        val result = reportService.rejectReport(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.AlreadyRejected, (result as Failure).value)
    }

    @Test
    fun `rejectReport - supervisor tries reject state waiting for manager returns AlreadyApproved`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)
        processService.submitProcess(testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR, processId)

        reportService.approveReport(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)

        val result = reportService.rejectReport(processId, testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.AlreadyApproved, (result as Failure).value)
    }

    // Unauthorized

    @Test
    fun `rejectReport - investigator cannot reject returns Unauthorized`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)

        val result = reportService.rejectReport(processId, testUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }

    @Test
    fun `rejectReport - triator cannot reject returns Unauthorized`() {
        val process = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(process is Success)
        val processId = (process as Success).value

        val reportId = createReportAndGetId(processId)

        val result = reportService.rejectReport(processId, testUtils.TRIATOR_ID, Roles.TRIATOR)
        assertTrue(result is Failure)
        assertEquals(ReportError.Unauthorized, (result as Failure).value)
    }
}