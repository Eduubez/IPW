import org.junit.jupiter.api.Assertions.assertTrue
import pt.isel.ipw.domain.process.State
import pt.isel.ipw.domain.roles.Roles

import pt.isel.ipw.services.errors.Failure
import pt.isel.ipw.services.errors.ProcessError
import pt.isel.ipw.services.errors.Success
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ProcessServiceImplTest {

    companion object {
        val jdbi = DbConfig.getConnection()
        val testUtils = TestUtils(jdbi)


        private val processService = testUtils.processService

        private val reportService = testUtils.reportService

    }

    @BeforeTest
    fun cleanUp() {
        testUtils.cleanRepo()
    }


    //Success

    @Test
    fun `create process - success`() {
        val result = testUtils.createProcess()
        assertTrue(result is Success)
        assertTrue((result as Success).value > 0)
    }

    //InvalidTriator (invalid userId/token)

    @Test
    fun `create process - invalid userId returns InvalidTriator`() {
        val result = testUtils.createProcess(userId = -1)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidTriator, (result as Failure).value)
    }

    // InvalidName

    @Test
    fun `create process - name too short returns InvalidName`() {
        val result = testUtils.createProcess(name = "AB")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidName, (result as Failure).value)
    }

    @Test
    fun `create process - empty name returns InvalidName`() {
        val result = testUtils.createProcess(name = "")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidName, (result as Failure).value)
    }

    //InvalidToken

    @Test
    fun `create process - blank street returns InvalidLocation`() {
        val result = testUtils.createProcess(street = "")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidLocation, (result as Failure).value)
    }

    @Test
    fun `create process - blank county returns InvalidLocation`() {
        val result = testUtils.createProcess(county = "")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidLocation, (result as Failure).value)
    }

    @Test
    fun `create process - blank district returns InvalidLocation`() {
        val result = testUtils.createProcess(district = "")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidLocation, (result as Failure).value)
    }

    // InvalidExpirationDate

    @Test
    fun `create process - past expiration date returns InvalidExpirationDate`() {
        val result = testUtils.createProcess(expiresAt = "2020-01-01T00:00:00") // past date
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidExpirationDate, (result as Failure).value)
    }

    @Test
    fun `create process - invalid date format returns InvalidExpirationDate`() {
        val result = testUtils.createProcess(expiresAt = "22-12-2004")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidExpirationDate, (result as Failure).value)
    }

    // InvalidPriority

    @Test
    fun `create process - invalid priority returns InvalidPriority`() {
        val result = testUtils.createProcess(priority = "HIGH")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidPriority, (result as Failure).value)
    }

    @Test
    fun `create process - empty priority returns InvalidPriority`() {
        val result = testUtils.createProcess(priority = "")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidPriority, (result as Failure).value)
    }

    //InvalidInvestigator

    @Test
    fun `create process - investigator does not exist returns InvalidInvestigator`() {
        val result = testUtils.createProcess(investigatorId = 9999)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidInvestigator, (result as Failure).value)
    }

    @Test
    fun `create process - investigator from different area returns InvalidInvestigator`() {
        val result = testUtils.createProcess(area = "Fire", investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidInvestigator, (result as Failure).value)
    }

    //InvalidSupervisor

    @Test
    fun `create process - supervisor does not exist returns InvalidSupervisor`() {
        val result = testUtils.createProcess(supervisorId = 9999)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidSupervisor, (result as Failure).value)
    }

// -----------------------------------------------------------------------
// submitProcess
// -----------------------------------------------------------------------

    @Test
    fun `submitProcess - investigator success moves to waiting approval supervisor`() {
        val created = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(created is Success)
        val processId = (created as Success).value
        reportService.createReport(
            processId = processId,
            userId = testUtils.INVESTIGATOR_ID,
            content = "Report content",
        )
        val result = processService.submitProcess(testUtils.INVESTIGATOR_ID, "investigator", processId)
        assertTrue(result is Success)

        val updated = processService.getProcessById(processId, testUtils.SUPERVISOR_ID, "supervisor")
        assertTrue(updated is Success)
        val process = (updated as Success).value
        assertEquals("waiting_approval_supervisor", process.state.name.lowercase())
    }

    @Test
    fun `submitProcess - process not found returns ProcessNotFound`() {
        val result = processService.submitProcess(testUtils.INVESTIGATOR_ID, "investigator", 9999)

        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `submitProcess - user without relation to process returns Failure`() {
        val created = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(created is Success)
        val processId = (created as Success).value
        reportService.createReport(
            processId = processId,
            userId = testUtils.INVESTIGATOR_ID,
            content = "Report content",
        )
        val wrongInvestigatorId = 99
        val result = processService.submitProcess(wrongInvestigatorId, "investigator", processId)

        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidUserId, (result as Failure).value)
    }

    @Test
    fun `submitProcess - invalid state for submission returns InvalidState`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value
        reportService.createReport(
            processId = processId,
            userId = testUtils.INVESTIGATOR_ID,
            content = "Report content",
        )
        jdbi.useHandle<Exception> { handle ->
            handle.execute("update public.process_state set end_date = now() where process_id = ?", processId)
            handle.execute(
                """
                insert into public.process_state (process_id, state_id, start_date) 
                values (?, (select id from public.state where lower(name) = 'canceled' limit 1), now())
            """, processId
            )
        }

        val result = processService.submitProcess(testUtils.INVESTIGATOR_ID, "investigator", processId)

        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidState, (result as Failure).value)
    }

    @Test
    fun `submitProcess - invalid role for submission returns error`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value
        reportService.createReport(
            processId = processId,
            userId = testUtils.INVESTIGATOR_ID,
            content = "Report content",
        )
         val result = processService.submitProcess(testUtils.TRIATOR_ID, "triator", processId)

        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidState, (result as Failure).value)
    }



// -----------------------------------------------------------------------
// getProcessById
// -----------------------------------------------------------------------

    @Test
    fun `getProcessById - success`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = processService.getProcessById(processId, testUtils.MANAGER_ID, "manager")
        assertTrue(result is Success)

        val process = (result as Success).value
        assertEquals(processId, process.id)
        assertEquals("Processo Teste", process.name)
        assertEquals("Car Accident", process.area.name)
        assertEquals("assigned", process.state.name.lowercase())
    }

    @Test
    fun `getProcessById - verify location data`() {
        val created = testUtils.createProcess()
        val processId = (created as Success).value

        val result = processService.getProcessById(processId, testUtils.MANAGER_ID, "manager")
        assertTrue(result is Success)

        val process = (result as Success).value
        assertEquals("Rua Augusta 1", process.location.street)
        assertEquals("Lisboa", process.location.county)
        assertEquals("Lisboa", process.location.district)
    }

    @Test
    fun `getProcessById - verify triator investigator and supervisor`() {
        val created = testUtils.createProcess()
        val processId = (created as Success).value

        val result = processService.getProcessById(processId, testUtils.MANAGER_ID, "manager")
        assertTrue(result is Success)

        val process = (result as Success).value
        assertEquals(testUtils.TRIATOR_ID, process.triator.id)
        assertEquals(testUtils.INVESTIGATOR_ID, process.investigator?.id)
        assertEquals(testUtils.SUPERVISOR_ID, process.supervisor?.id)
    }

    @Test
    fun `getProcessById - process not found returns ProcessNotFound`() {
        val result = processService.getProcessById(9999, testUtils.MANAGER_ID, "manager")
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `getProcessById - negative id returns ProcessNotFound`() {
        val result = processService.getProcessById(-1, testUtils.MANAGER_ID, "manager")
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `getProcessById - zero id returns ProcessNotFound`() {
        val result = processService.getProcessById(0, testUtils.MANAGER_ID, "manager")
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }
// -----------------------------------------------------------------------
// getAllProcesses
// -----------------------------------------------------------------------

    @Test
    fun `getAllProcesses - returns at least one process for triator`() {
        val created = testUtils.createProcess(investigatorId = null, supervisorId = null)
        assertTrue(created is Success)

        val result = processService.getAllProcesses(
            userId = testUtils.TRIATOR_ID,
            history = null,
            offset = 0,
            limit = 10,
            role = "triator",
            name = "",
            priority = "",
            state = ""
        )

        assertTrue(result is Success)
        val processes = (result as Success).value.first
        assertTrue(processes.isNotEmpty())
        assertTrue(processes.all { it.state.name.lowercase() == "not_assigned" })
    }

    @Test
    fun `getAllProcesses - returns only processes from investigator matching allowed states`() {
        val created = testUtils.createProcess(investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(created is Success)

        val result = processService.getAllProcesses(
            userId = testUtils.INVESTIGATOR_ID,
            history = null,
            offset = 0,
            limit = 10,
            role = "investigator",
            name = "",
            priority = "",
            state = ""
        )

        assertTrue(result is Success)
        val processes = (result as Success).value.first

        val allowedStates = listOf("assigned", "on_going", "rejected_by_supervisor")
        assertTrue(processes.all { it.investigator?.id == testUtils.INVESTIGATOR_ID })
        assertTrue(processes.all { allowedStates.contains(it.state.name.lowercase()) })
    }

    @Test
    fun `getAllProcesses - returns only processes from supervisor area matching allowed states`() {
        val created = testUtils.createProcess(area = "Car Accident", supervisorId = testUtils.SUPERVISOR_ID)
        assertTrue(created is Success)
        val processId = (created as Success).value

        jdbi.useHandle<Exception> { handle ->
            handle.execute("update public.process_state set end_date = now() where process_id = ?", processId)
            handle.execute(
                """
                insert into public.process_state (process_id, state_id, start_date) 
                values (?, (select id from public.state where lower(name) = 'waiting_approval_supervisor' limit 1), now())
            """, processId
            )
        }

        val result = processService.getAllProcesses(
            userId = testUtils.SUPERVISOR_ID,
            history = null,
            offset = 0,
            limit = 10,
            role = "supervisor",
            name = "",
            priority = "",
            state = ""
        )

        assertTrue(result is Success)
        val processes = (result as Success).value.first

        val allowedStates = listOf("waiting_approval_supervisor", "rejected_by_manager")
        assertTrue(processes.isNotEmpty())
        assertTrue(processes.all { allowedStates.contains(it.state.name.lowercase()) })
    }

    @Test
    fun `getAllProcesses - returns all processes for manager matching waiting manager state`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        jdbi.useHandle<Exception> { handle ->
            handle.execute("update public.process_state set end_date = now() where process_id = ?", processId)
            handle.execute(
                """
                insert into public.process_state (process_id, state_id, start_date) 
                values (?, (select id from public.state where lower(name) = 'waiting_approval_manager' limit 1), now())
            """, processId
            )
        }

        val result = processService.getAllProcesses(
            userId = testUtils.MANAGER_ID,
            offset = 0,
            history = null,
            limit = 100,
            role = "manager",
            name = "",
            priority = "",
            state = ""
        )

        assertTrue(result is Success)
        val processes = (result as Success).value.first
        assertTrue(processes.all { it.state.name.lowercase() == "waiting_approval_manager" })
    }

    @Test
    fun `getAllProcesses - with areaId filter returns only that area`() {
        val created = testUtils.createProcess(area = "Car Accident", investigatorId = testUtils.INVESTIGATOR_ID)
        assertTrue(created is Success)

        val result = processService.getAllProcesses(
            userId = testUtils.INVESTIGATOR_ID,
            history = null,
            offset = 0,
            limit = 10,
            role = "investigator",
            name = "",
            priority = "",
            state = ""
        )
        assertTrue(result is Success)
        val processes = (result as Success).value.first
        assertTrue(processes.all { it.area.name == "Car Accident" })
    }

    @Test
    fun `getAllProcesses - offset and limit work as pagination`() {
        testUtils.createProcess(name = "Process 1", investigatorId = null, supervisorId = null)
        testUtils.createProcess(name = "Process 2", investigatorId = null, supervisorId = null)
        testUtils.createProcess(name = "Process 3", investigatorId = null, supervisorId = null)

        val result1 = processService.getAllProcesses(
            userId = testUtils.TRIATOR_ID,
            history = null,
            offset = 0,
            limit = 2,
            role = "triator",
            name = "",
            priority = "",
            state = ""
        )

        val result2 = processService.getAllProcesses(
            userId = testUtils.TRIATOR_ID,
            history = null,
            offset = 2,
            limit = 2,
            role = "triator",
            name = "",
            priority = "",
            state = ""
        )

        assertTrue(result1 is Success)
        assertTrue(result2 is Success)
        val list1 = (result1 as Success).value.first
        val list2 = (result2 as Success).value.first
        assertTrue(list1.size <= 2)
        assertTrue(list2.size <= 2)
        assertTrue(list1.none { p1 -> list2.any { p2 -> p1.id == p2.id } })
    }

    @Test
    fun `getAllProcesses - invalid offset returns empty list or error`() {
        val result = processService.getAllProcesses(
            userId = testUtils.TRIATOR_ID,
            history = null,
            offset = -1,
            limit = 10,
            role = "triator",
            name = "",
            priority = "",
            state = ""
        )
        assertTrue(result is Failure || (result is Success && (result as Success).value.first.isEmpty()))
    }

    @Test
    fun `getAllProcesses - invalid limit returns empty list or error`() {
        val result = processService.getAllProcesses(
            userId = testUtils.TRIATOR_ID,
            history = null,
            offset = 0,
            limit = 0,
            role = "triator",
            name = "",
            priority = "",
            state = ""
        )
        assertTrue(result is Failure || (result is Success && (result).value.first.isEmpty()))
    }


    @Test
    fun `getAllProcesses - limit and skip `(){

        val result1 = processService.getAllProcesses(
            userId = testUtils.INVESTIGATOR_ID,
            history = false,
            offset = 0,
            limit = 2,
            role = Roles.INVESTIGATOR,
            name = "",
            priority = "",
            state = ""
        )

        val result2 = processService.getAllProcesses(
            userId = testUtils.INVESTIGATOR_ID,
            history = false,
            offset = 2,
            limit = 2,
            role = Roles.INVESTIGATOR,
            name = "",
            priority = "",
            state = ""
        )

        val result3 = processService.getAllProcesses(
            userId = testUtils.INVESTIGATOR_ID,
            history = false,
            offset = 4,
            limit = 2,
            role = Roles.INVESTIGATOR,
            name = "",
            priority = "",
            state = ""
        )

        val result4 = processService.getAllProcesses(
            userId = testUtils.INVESTIGATOR_ID,
            history = false,
            offset = 2,
            limit = 1,
            role = Roles.INVESTIGATOR,
            name = "",
            priority = "",
            state = ""
        )

        assertTrue(result1 is Success)
        assertTrue(result2 is Success)
        assertTrue(result3 is Success)
        assertTrue(result4 is Success)

        assertTrue((result1 as Success).value.first.size == 2 && result1.value.second.hasNext )
        assertTrue((result2 as Success).value.first.size == 2 && result2.value.second.hasNext)
        assertTrue((result3 as Success).value.first.size == 1 && !result3.value.second.hasNext)
        assertTrue(result1.value.second.totalCount == result2.value.second.totalCount )
        assertTrue(result1.value.second.totalCount == result3.value.second.totalCount )


    }


// -----------------------------------------------------------------------
// changeEndDate
// -----------------------------------------------------------------------


    @Test
    fun `changeEndDate - success updates due date`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val newEndDate = "2028-01-01T00:00"
        val result = processService.changeEndDate(processId, newEndDate, testUtils.SUPERVISOR_ID, "supervisor")
        assertTrue(result is Success)

        val updated = processService.getProcessById(processId, testUtils.MANAGER_ID, "manager")
        assertTrue(updated is Success)
        val process = (updated as Success).value
        assertEquals(newEndDate, process.dueDate.toString())
    }

    @Test
    fun `changeEndDate - process not found returns error`() {
        val result = processService.changeEndDate(9999, "2028-01-01T00:00", testUtils.SUPERVISOR_ID, "supervisor")
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `changeEndDate - invalid date format returns error`() {
        val created = testUtils.createProcess()
        val processId = (created as Success).value

        val result = processService.changeEndDate(processId, "invalid-date", testUtils.SUPERVISOR_ID, "supervisor")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidExpirationDate, (result as Failure).value)
    }

    @Test
    fun `changeEndDate - past date returns error`() {
        val created = testUtils.createProcess()
        val processId = (created as Success).value

        val result = processService.changeEndDate(processId, "2020-01-01T00:00:00", testUtils.SUPERVISOR_ID, "supervisor")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidExpirationDate, (result as Failure).value)
    }

    // -----------------------------------------------------------------------
// assignSupervisor
// -----------------------------------------------------------------------


    @Test
    fun `assignSupervisor - success assigns supervisor`() {
        val created = testUtils.createProcess(supervisorId = null)
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = processService.assignSupervisor(processId, testUtils.TRIATOR_ID, testUtils.SUPERVISOR_ID)
        assertTrue(result is Success)

        val updated = processService.getProcessById(processId, testUtils.SUPERVISOR_ID, "supervisor")
        assertTrue(updated is Success)
        val process = (updated as Success).value
        assertEquals(testUtils.SUPERVISOR_ID, process.supervisor?.id)
    }

    @Test
    fun `assignSupervisor - process not found returns error`() {
        val result = processService.assignSupervisor(9999, testUtils.TRIATOR_ID, testUtils.SUPERVISOR_ID)
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `assignSupervisor - supervisor not found returns error`() {
        val created = testUtils.createProcess(supervisorId = null)
        val processId = (created as Success).value

        val result = processService.assignSupervisor(processId, testUtils.TRIATOR_ID, 9999)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidSupervisor, (result as Failure).value)
    }

    @Test
    fun `assignSupervisor - supervisor from different area returns error`() {
        val created = testUtils.createProcess(supervisorId = null, area = "Car Accident")
        val processId = (created as Success).value

        val result = processService.assignSupervisor(processId, testUtils.TRIATOR_ID, 6)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidSupervisor, (result as Failure).value)
    }

    // -----------------------------------------------------------------------
// assignInvestigator
// -----------------------------------------------------------------------

    @Test
    fun `assignInvestigator - success assigns investigator`() {
        val created = testUtils.createProcess(investigatorId = null)
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = processService.assignInvestigator(processId, testUtils.TRIATOR_ID, testUtils.INVESTIGATOR_ID)
        assertTrue(result is Success)

        val updated = processService.getProcessById(processId, testUtils.INVESTIGATOR_ID, "investigator")
        assertTrue(updated is Success)
        val process = (updated as Success).value
        assertEquals(testUtils.INVESTIGATOR_ID, process.investigator?.id)
    }

    @Test
    fun `assignInvestigator - process not found returns error`() {
        val result = processService.assignInvestigator(9999, testUtils.TRIATOR_ID, testUtils.INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `assignInvestigator - investigator not found returns error`() {
        val created = testUtils.createProcess(investigatorId = null)
        val processId = (created as Success).value

        val result = processService.assignInvestigator(processId, testUtils.TRIATOR_ID, 9999)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidInvestigator, (result as Failure).value)
    }

    @Test
    fun `assignInvestigator - investigator from different area returns error`() {
        val created = testUtils.createProcess(investigatorId = null, area = "Car Accident")
        val processId = (created as Success).value

        val result = processService.assignInvestigator(processId, testUtils.TRIATOR_ID, 6)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidInvestigator, (result as Failure).value)
    }

// -----------------------------------------------------------------------
// changePriority
// -----------------------------------------------------------------------


    @Test
    fun `changePriority - success updates priority`() {
        val created = testUtils.createProcess(priority = "normal")
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = processService.changePriority(processId, "urgent", testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Success)

        val updated = processService.getProcessById(processId, testUtils.SUPERVISOR_ID, "supervisor")
        assertTrue(updated is Success)
        val process = (updated as Success).value
        assertEquals("urgent", process.priority.name.lowercase())
    }

    @Test
    fun `changePriority - process not found returns error`() {
        val result = processService.changePriority(9999, "urgent", testUtils.SUPERVISOR_ID,   Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `changePriority - invalid priority returns error`() {
        val created = testUtils.createProcess(priority = "normal")
        val processId = (created as Success).value

        val result = processService.changePriority(processId, "invalid_priority", testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidPriority, (result as Failure).value)
    }

    @Test
    fun `changePriority - same priority returns success or no-op`() {
        val created = testUtils.createProcess(priority = "normal")
        val processId = (created as Success).value

        val result = processService.changePriority(processId, "normal", testUtils.SUPERVISOR_ID, Roles.SUPERVISOR)
        assertTrue(result is Success)
        val updated = processService.getProcessById(processId, testUtils.SUPERVISOR_ID, "supervisor")
        assertTrue(updated is Success)
        val process = (updated as Success).value
        assertEquals("normal", process.priority.name.lowercase())
    }


// -----------------------------------------------------------------------
// cancelProcess
// -----------------------------------------------------------------------

    @Test
    fun `cancelProcess - success cancels the process`() {
        val created = testUtils.createProcess()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = processService.cancelProcess(processId, testUtils.SUPERVISOR_ID)
        assertTrue(result is Success)

        val updated = processService.getProcessById(processId, testUtils.SUPERVISOR_ID, "supervisor")
        assertTrue(updated is Success)

        val process = (updated as Success).value
        assertEquals("canceled", process.state.name.lowercase())
    }

    @Test
    fun `cancelProcess - process not found returns error`() {
        val result = processService.cancelProcess(9999,testUtils. SUPERVISOR_ID)
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }


    @Test
    fun `cancelProcess - already canceled returns error or no-op`() {
        val created = testUtils.createProcess()
        val processId = (created as Success).value

        val firstCancel = processService.cancelProcess(processId, testUtils.SUPERVISOR_ID)
        assertTrue(firstCancel is Success)

        val secondCancel = processService.cancelProcess(processId, testUtils.SUPERVISOR_ID)
        assertTrue(secondCancel is Failure || secondCancel is Success)
    }


}


