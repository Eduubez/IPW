package pt.isel.ipw.http

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import pt.isel.ipw.domain.DTO.input.CreateReportRequest
import pt.isel.ipw.domain.DTO.output.CreateReportResponse
import pt.isel.ipw.domain.report.Report
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.http.controllers.ReportController
import pt.isel.ipw.http.errors.ErrorCode
import pt.isel.ipw.http.errors.Problem
import kotlin.test.Test

@SpringJUnitConfig(TestConfig::class)
class ReportControllerTest {

    @Autowired
    private lateinit var reportController: ReportController

    @AfterEach
    fun clearSecurityContext() {
        TestUtils.clearSecurityContext()
        TestUtils.clear(DbConfig.getConnection())
    }

     private fun createReport(processId: Int, content: String = "Valid report content"): Int {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)
        val resp = reportController.createReport(processId, CreateReportRequest(content))
        return (resp.body as CreateReportResponse).id
    }

    // ── createReport ──────────────────────────────────────────────────────────

    @Test
    fun `createReport - success`() {
         TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = reportController.createReport(2, CreateReportRequest("Valid report content here"))
        val body = resp.body as CreateReportResponse

        assertEquals(201, resp.statusCode.value())
        assertNotNull(body.id)
    }

    @Test
    fun `createReport - process not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = reportController.createReport(9999, CreateReportRequest("Valid report content"))
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/process-not-found", error.type)
        assertEquals(ErrorCode.PROCESS_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `createReport - invalid content blank`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = reportController.createReport(1, CreateReportRequest("   "))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-content", error.type)
        assertEquals(ErrorCode.INVALID_CONTENT, error.errorCode)
    }

    @Test
    fun `createReport - unauthorized not the investigator`() {
         TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.INVESTIGATOR)

        val resp = reportController.createReport(1, CreateReportRequest("Valid report content"))
        val error = resp.body as Problem

        assertEquals(403, resp.statusCode.value())
        assertEquals("problems/unauthorized", error.type)
        assertEquals(ErrorCode.UNAUTHORIZED, error.errorCode)
    }

    // ── getByProcessId ────────────────────────────────────────────────────────

    @Test
    fun `getByProcessId - success as investigator`() {
        createReport(3)
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = reportController.getByProcessId(3)

        assertEquals(200, resp.statusCode.value())
        assertNotNull(resp.body as? Report)
    }

    @Test
    fun `getByProcessId - success as supervisor`() {
        createReport(3)
         TestUtils.setUpSecurityContext(userId = TestUtils.SUPERVISOR_ID, role = Roles.SUPERVISOR)

        val resp = reportController.getByProcessId(3)

        assertEquals(200, resp.statusCode.value())
    }

    @Test
    fun `getByProcessId - success as manager`() {
        createReport(3)
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = reportController.getByProcessId(3)

        assertEquals(200, resp.statusCode.value())
    }

    @Test
    fun `getByProcessId - process not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = reportController.getByProcessId(9999)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/process-not-found", error.type)
        assertEquals(ErrorCode.PROCESS_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `getByProcessId - report not found`() {
          TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = reportController.getByProcessId(8)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/report-not-found", error.type)
        assertEquals(ErrorCode.REPORT_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `getByProcessId - not associated`() {
         TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.INVESTIGATOR)

        val resp = reportController.getByProcessId(1)
        val error = resp.body as Problem

        assertEquals(403, resp.statusCode.value())
        assertEquals("problems/not-associated", error.type)
        assertEquals(ErrorCode.NOT_ASSOCIATED, error.errorCode)
    }

    // ── updateReport ──────────────────────────────────────────────────────────

    @Test
    fun `updateReport - success`() {
        createReport(4)
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = reportController.updateReport(4, CreateReportRequest("Updated report content here"))

        assertEquals(200, resp.statusCode.value())
    }

    @Test
    fun `updateReport - process not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = reportController.updateReport(9999, CreateReportRequest("Valid report content"))
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/process-not-found", error.type)
        assertEquals(ErrorCode.PROCESS_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `updateReport - invalid content blank`() {
          TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = reportController.updateReport(1, CreateReportRequest("   "))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-content", error.type)
        assertEquals(ErrorCode.INVALID_CONTENT, error.errorCode)
    }

    @Test
    fun `updateReport - report not found`() {
         TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = reportController.updateReport(1, CreateReportRequest("Valid report content"))
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/report-not-found", error.type)
        assertEquals(ErrorCode.REPORT_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `updateReport - unauthorized investigator`() {
         createReport(4)
         TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.INVESTIGATOR)

        val resp = reportController.updateReport(4, CreateReportRequest("Trying to update someone else report"))
        val error = resp.body as Problem

        assertEquals(403, resp.statusCode.value())
        assertEquals("problems/unauthorized-investigator", error.type)
        assertEquals(ErrorCode.UNAUTHORIZED_INVESTIGATOR, error.errorCode)
    }

    // ── approveReport ─────────────────────────────────────────────────────────

    @Test
    fun `approveReport - process not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = reportController.approveReport(9999)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/process-not-found", error.type)
        assertEquals(ErrorCode.PROCESS_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `approveReport - report not found`() {
         TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = reportController.approveReport(8)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/report-not-found", error.type)
        assertEquals(ErrorCode.REPORT_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `approveReport - unauthorized not supervisor or manager`() {
         createReport(2)
         TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = reportController.approveReport(2)
        val error = resp.body as Problem

        assertEquals(403, resp.statusCode.value())
        assertEquals("problems/unauthorized", error.type)
        assertEquals(ErrorCode.UNAUTHORIZED, error.errorCode)
    }

    @Test
    fun `approveReport - invalid state supervisor wrong process state`() {
          createReport(3)
        TestUtils.setUpSecurityContext(userId = TestUtils.SUPERVISOR_ID, role = Roles.SUPERVISOR)

        val resp = reportController.approveReport(3)
        val error = resp.body as Problem

         assertEquals(409, resp.statusCode.value())
        assertEquals("problems/internal-server-error", error.type)
        assertEquals(ErrorCode.INTERNAL_ERROR, error.errorCode)
    }

    // ── rejectReport ──────────────────────────────────────────────────────────

    @Test
    fun `rejectReport - process not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = reportController.rejectReport(9999)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/process-not-found", error.type)
        assertEquals(ErrorCode.PROCESS_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `rejectReport - report not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = reportController.rejectReport(8)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/report-not-found", error.type)
        assertEquals(ErrorCode.REPORT_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `rejectReport - unauthorized not supervisor or manager`() {
         createReport(4)
         TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = reportController.rejectReport(4)
        val error = resp.body as Problem

        assertEquals(403, resp.statusCode.value())
        assertEquals("problems/unauthorized", error.type)
        assertEquals(ErrorCode.UNAUTHORIZED, error.errorCode)
    }

    @Test
    fun `rejectReport - invalid state supervisor wrong process state`() {

        createReport(4)
        TestUtils.setUpSecurityContext(userId = TestUtils.SUPERVISOR_ID, role = Roles.SUPERVISOR)

        val resp = reportController.rejectReport(4)
        val error = resp.body as Problem

         assertEquals(409, resp.statusCode.value())
        assertEquals("problems/internal-server-error", error.type)
        assertEquals(ErrorCode.INTERNAL_ERROR, error.errorCode)
    }
}