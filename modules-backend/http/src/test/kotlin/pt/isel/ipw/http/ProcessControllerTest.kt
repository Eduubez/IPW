package pt.isel.ipw.http

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import pt.isel.ipw.domain.DTO.input.UpdatePriorityRequest
import pt.isel.ipw.domain.DTO.output.CreateProcessResponse
import pt.isel.ipw.domain.DTO.output.ListResponse
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.http.controllers.ProcessController
import pt.isel.ipw.http.errors.ErrorCode
import pt.isel.ipw.http.errors.Problem
import kotlin.test.Test

@SpringJUnitConfig(TestConfig::class)
class ProcessControllerTest {

    @Autowired
    private lateinit var processController: ProcessController

    @AfterEach
    fun clearSecurityContext() {
        TestUtils.clearSecurityContext()
    }

    // ── createProcess ─────────────────────────────────────────────────────────

    @Test
    fun `createProcess - success`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = processController.createProcess(TestUtils.validCreateRequest())
        val body = resp.body as CreateProcessResponse

        assertEquals(201, resp.statusCode.value())
        assertNotNull(body.id)
    }

    @Test
    fun `createProcess - invalid name too short`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = processController.createProcess(TestUtils.validCreateRequest().copy(name = "abc"))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-process-name", error.type)
        assertEquals(ErrorCode.INVALID_PROCESS_NAME, error.errorCode)
    }

    @Test
    fun `createProcess - invalid location blank street`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = processController.createProcess(TestUtils.validCreateRequest().copy(street = "   "))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-location", error.type)
        assertEquals(ErrorCode.INVALID_LOCATION, error.errorCode)
    }

    @Test
    fun `createProcess - invalid expiration date past date`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = processController.createProcess(TestUtils.validCreateRequest().copy(expiresAt = "2020-01-01T00:00:00"))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-expiration-date", error.type)
        assertEquals(ErrorCode.INVALID_EXPIRATION_DATE, error.errorCode)
    }

    @Test
    fun `createProcess - invalid priority`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = processController.createProcess(TestUtils.validCreateRequest().copy(priority = "unknown_priority"))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-priority", error.type)
        assertEquals(ErrorCode.INVALID_PRIORITY, error.errorCode)
    }

    @Test
    fun `createProcess - invalid investigator wrong area`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = processController.createProcess(TestUtils.validCreateRequest().copy(investigatorId = TestUtils.ADMIN_ID))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-investigator", error.type)
        assertEquals(ErrorCode.INVALID_INVESTIGATOR, error.errorCode)
    }

    @Test
    fun `createProcess - invalid supervisor wrong area`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = processController.createProcess(TestUtils.validCreateRequest().copy(supervisorId = TestUtils.ADMIN_ID))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-supervisor", error.type)
        assertEquals(ErrorCode.INVALID_SUPERVISOR, error.errorCode)
    }

    // ── getProcessById ────────────────────────────────────────────────────────

    @Test
    fun `getProcessById - success as investigator`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = processController.getProcessById(1)

        assertEquals(200, resp.statusCode.value())
        assertNotNull(resp.body)
    }

    @Test
    fun `getProcessById - success as supervisor`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.SUPERVISOR_ID, role = Roles.SUPERVISOR)

        val resp = processController.getProcessById(1)

        assertEquals(200, resp.statusCode.value())
    }

    @Test
    fun `getProcessById - success as manager`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = processController.getProcessById(1)

        assertEquals(200, resp.statusCode.value())
    }

    @Test
    fun `getProcessById - process not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = processController.getProcessById(9999)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/process-not-found", error.type)
        assertEquals(ErrorCode.PROCESS_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `getProcessById - unauthorized user not associated to process`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = processController.getProcessById(8)
        val error = resp.body as Problem

        assertEquals(403, resp.statusCode.value())
        assertEquals("problems/unauthorized", error.type)
        assertEquals(ErrorCode.UNAUTHORIZED, error.errorCode)
    }

    // ── submitProcess ─────────────────────────────────────────────────────────

    @Test
    fun `submitProcess - process not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = processController.submitProcess(9999)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/process-not-found", error.type)
        assertEquals(ErrorCode.PROCESS_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `submitProcess - report not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = processController.submitProcess(1)
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/report-not-found", error.type)
        assertEquals(ErrorCode.REPORT_NOT_FOUND, error.errorCode)
    }

    // ── getAllProcesses ────────────────────────────────────────────────────────

    @Test
    fun `getAllProcesses - success as investigator`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = processController.getAllProcesses(null, null, null)

        assertEquals(200, resp.statusCode.value())
        assertNotNull(resp.body as? ListResponse<*>)
    }

    @Test
    fun `getAllProcesses - success as triator`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = processController.getAllProcesses(null, null, null)

        assertEquals(200, resp.statusCode.value())
        assertNotNull(resp.body as? ListResponse<*>)
    }

    @Test
    fun `getAllProcesses - success as manager`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = processController.getAllProcesses(null, null, null)

        assertEquals(200, resp.statusCode.value())
    }

    @Test
    fun `getAllProcesses - invalid limit`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = processController.getAllProcesses(null, 0, null)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/invalid-limit", error.type)
        assertEquals(ErrorCode.INVALID_LIMIT, error.errorCode)
    }

    @Test
    fun `getAllProcesses - invalid offset`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = processController.getAllProcesses(-1, null, null)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/invalid-offset", error.type)
        assertEquals(ErrorCode.INVALID_OFFSET, error.errorCode)
    }


    // ── updateProcessEndDate ──────────────────────────────────────────────────

    @Test
    fun `updateProcessEndDate - success as supervisor`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.SUPERVISOR_ID, role = Roles.SUPERVISOR)

        val resp = processController.updateProcessEndDate(1, TestUtils.VALID_FUTURE_DATE)

        assertEquals(204, resp.statusCode.value())
    }

    @Test
    fun `updateProcessEndDate - success as manager`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = processController.updateProcessEndDate(1, TestUtils.VALID_FUTURE_DATE)

        assertEquals(204, resp.statusCode.value())
    }

    @Test
    fun `updateProcessEndDate - process not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = processController.updateProcessEndDate(9999, TestUtils.VALID_FUTURE_DATE)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/process-not-found", error.type)
        assertEquals(ErrorCode.PROCESS_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `updateProcessEndDate - invalid date past date`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = processController.updateProcessEndDate(1, "2020-01-01T00:00:00")
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-expiration-date", error.type)
        assertEquals(ErrorCode.INVALID_EXPIRATION_DATE, error.errorCode)
    }

    @Test
    fun `updateProcessEndDate - unauthorized user is not supervisor of process`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.SUPERVISOR)

        val resp = processController.updateProcessEndDate(1, TestUtils.VALID_FUTURE_DATE)
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-supervisor", error.type)
        assertEquals(ErrorCode.INVALID_SUPERVISOR, error.errorCode)
    }

    // ── assignInvestigator ────────────────────────────────────────────────────

    @Test
    fun `assignInvestigator - success`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = processController.assignInvestigator(6, TestUtils.INVESTIGATOR_ID)

        assertEquals(204, resp.statusCode.value())
    }

    @Test
    fun `assignInvestigator - process not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = processController.assignInvestigator(9999, TestUtils.INVESTIGATOR_ID)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/process-not-found", error.type)
        assertEquals(ErrorCode.PROCESS_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `assignInvestigator - invalid triator not the process triator`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.TRIATOR)

        val resp = processController.assignInvestigator(1, TestUtils.INVESTIGATOR_ID)
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-triator", error.type)
        assertEquals(ErrorCode.INVALID_TRIATOR, error.errorCode)
    }

    @Test
    fun `assignInvestigator - invalid investigator wrong area`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = processController.assignInvestigator(1, TestUtils.ADMIN_ID)
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-investigator", error.type)
        assertEquals(ErrorCode.INVALID_INVESTIGATOR, error.errorCode)
    }

    // ── assignSupervisor ──────────────────────────────────────────────────────

    @Test
    fun `assignSupervisor - success`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = processController.assignSupervisor(7, TestUtils.SUPERVISOR_ID)

        assertEquals(204, resp.statusCode.value())
    }

    @Test
    fun `assignSupervisor - process not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = processController.assignSupervisor(9999, TestUtils.SUPERVISOR_ID)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/process-not-found", error.type)
        assertEquals(ErrorCode.PROCESS_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `assignSupervisor - invalid triator not the process triator`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.TRIATOR)

        val resp = processController.assignSupervisor(1, TestUtils.SUPERVISOR_ID)
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-triator", error.type)
        assertEquals(ErrorCode.INVALID_TRIATOR, error.errorCode)
    }

    @Test
    fun `assignSupervisor - invalid supervisor wrong area`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = processController.assignSupervisor(1, TestUtils.ADMIN_ID)
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-supervisor", error.type)
        assertEquals(ErrorCode.INVALID_SUPERVISOR, error.errorCode)
    }

    // ── changePriority ────────────────────────────────────────────────────────

    @Test
    fun `changePriority - success as supervisor`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.SUPERVISOR_ID, role = Roles.SUPERVISOR)

        val resp = processController.changePriority(1, UpdatePriorityRequest(priority = "urgent"))

        assertEquals(204, resp.statusCode.value())
    }

    @Test
    fun `changePriority - success as manager`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = processController.changePriority(1, UpdatePriorityRequest(priority = "normal"))

        assertEquals(204, resp.statusCode.value())
    }

    @Test
    fun `changePriority - process not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = processController.changePriority(9999, UpdatePriorityRequest(priority = "normal"))
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/process-not-found", error.type)
        assertEquals(ErrorCode.PROCESS_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `changePriority - invalid priority`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = processController.changePriority(1, UpdatePriorityRequest(priority = "super_high"))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-priority", error.type)
        assertEquals(ErrorCode.INVALID_PRIORITY, error.errorCode)
    }

    @Test
    fun `changePriority - unauthorized user is not supervisor of process`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.SUPERVISOR)

        val resp = processController.changePriority(1, UpdatePriorityRequest(priority = "normal"))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-supervisor", error.type)
        assertEquals(ErrorCode.INVALID_SUPERVISOR, error.errorCode)
    }

    // ── cancelProcess ─────────────────────────────────────────────────────────

    @Test
    fun `cancelProcess - success`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = processController.cancelProcess(5)

        assertEquals(204, resp.statusCode.value())
    }

    @Test
    fun `cancelProcess - process not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.MANAGER_ID, role = Roles.MANAGER)

        val resp = processController.cancelProcess(9999)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/process-not-found", error.type)
        assertEquals(ErrorCode.PROCESS_NOT_FOUND, error.errorCode)
    }
}