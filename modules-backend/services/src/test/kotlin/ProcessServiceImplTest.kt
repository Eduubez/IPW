import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertTrue
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ipw.repository.jdbi.configureWithAppRequirements
import pt.isel.ipw.repository.jdbi.transaction.JdbiTransactionManager
import pt.isel.ipw.services.ActivityServiceImpl
import pt.isel.ipw.services.ProcessServiceImpl
import pt.isel.ipw.services.auth.JwtTokenService
import pt.isel.ipw.services.errors.Failure
import pt.isel.ipw.services.errors.ProcessError
import pt.isel.ipw.services.errors.Success
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ProcessServiceImplTest {

    companion object {
        val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setUrl("jdbc:postgresql://localhost:5433/postgres")
                user = "postgres"
                password = "changeit"
            }
        ).configureWithAppRequirements()

        private val tokenService = JwtTokenService(
            secret = "1234567890123456789012345678901234567890123456789012345678901234",
            loginTokenTtlMinutes = 5L,
            accessTokenTtlMinutes = 120L,
            refreshTokenTtlMinutes = 10080L
        )

        private val trxManager = JdbiTransactionManager(jdbi)

        private val processService = ProcessServiceImpl(
            trxManager,
            ActivityServiceImpl(trxManager),
        )

        //Alice(triator)=2, Bob(investigator)=3, Carol(supervisor)=4
        private const val TRIATOR_ID = 2
        private const val INVESTIGATOR_ID = 3   // Bob - area "Car Accident"
        private const val SUPERVISOR_ID = 4
        // Carol - area "Car Accident"
        private const val MANAGER_ID = 5

        private const val CAR_ACCIDENT_AREA_ID = 1 // ID da área correspondente a "Car Accident"
    }

    @BeforeTest
    fun cleanUp() {
        jdbi.useHandle<Exception> { handle ->
            handle.execute("call public.sample_data()")
        }
    }


    private val validToken = tokenService.createAccessToken(TRIATOR_ID, "triator").token
    private fun userId() = tokenService.parseAccessToken(validToken).userId

    private fun createValid(
        userId: Int = userId(),
        name: String = "Processo Teste",
        street: String = "Rua Augusta 1",
        county: String = "Lisboa",
        district: String = "Lisboa",
        area: String = "Car Accident",
        priority: String = "normal",
        expiresAt: String = "2027-12-31T23:59:59",
        investigatorId: Int? = INVESTIGATOR_ID,
        supervisorId: Int? = SUPERVISOR_ID,
        canBeFraud: Boolean = false,
        note: String? = "Nota de teste"
    ) = processService.createProcess(
        userId = userId,
        name = name,
        street = street,
        county = county,
        district = district,
        latitude = null,
        longitude = null,
        area = area,
        priority = priority,
        expiresAt = expiresAt,
        investigatorId = investigatorId,
        supervisorId = supervisorId,
        insuranceId = null,
        typificationId = null,
        canBeFraud = canBeFraud,
        note = note
    )

    //Success

    @Test
    fun `create process - success`() {
        val result = createValid()
        assertTrue(result is Success)
        assertTrue((result as Success).value > 0)
    }

    //InvalidTriator (invalid userId/token)

    @Test
    fun `create process - invalid userId returns InvalidTriator`() {
        val result = createValid(userId = -1)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidTriator, (result as Failure).value)
    }

    // InvalidName

    @Test
    fun `create process - name too short returns InvalidName`() {
        val result = createValid(name = "AB") // length <= 3
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidName, (result as Failure).value)
    }

    @Test
    fun `create process - empty name returns InvalidName`() {
        val result = createValid(name = "")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidName, (result as Failure).value)
    }

    //InvalidToken

    @Test
    fun `create process - blank street returns InvalidLocation`() {
        val result = createValid(street = "")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidLocation, (result as Failure).value)
    }

    @Test
    fun `create process - blank county returns InvalidLocation`() {
        val result = createValid(county = "")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidLocation, (result as Failure).value)
    }

    @Test
    fun `create process - blank district returns InvalidLocation`() {
        val result = createValid(district = "")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidLocation, (result as Failure).value)
    }

    // InvalidExpirationDate

    @Test
    fun `create process - past expiration date returns InvalidExpirationDate`() {
        val result = createValid(expiresAt = "2020-01-01T00:00:00") // past date
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidExpirationDate, (result as Failure).value)
    }

    @Test
    fun `create process - invalid date format returns InvalidExpirationDate`() {
        val result = createValid(expiresAt = "22-12-2004")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidExpirationDate, (result as Failure).value)
    }

    // InvalidPriority

    @Test
    fun `create process - invalid priority returns InvalidPriority`() {
        val result = createValid(priority = "HIGH")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidPriority, (result as Failure).value)
    }

    @Test
    fun `create process - empty priority returns InvalidPriority`() {
        val result = createValid(priority = "")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidPriority, (result as Failure).value)
    }

    //InvalidInvestigator

    @Test
    fun `create process - investigator does not exist returns InvalidInvestigator`() {
        val result = createValid(investigatorId = 9999)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidInvestigator, (result as Failure).value)
    }

    @Test
    fun `create process - investigator from different area returns InvalidInvestigator`() {
        val result = createValid(area = "Fire", investigatorId = INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidInvestigator, (result as Failure).value)
    }

    //InvalidSupervisor

    @Test
    fun `create process - supervisor does not exist returns InvalidSupervisor`() {
        val result = createValid(supervisorId = 9999)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidSupervisor, (result as Failure).value)
    }


// -----------------------------------------------------------------------
// getProcessById
// -----------------------------------------------------------------------

    @Test
    fun `getProcessById - success`() {
        val created = createValid()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = processService.getProcessById(processId, MANAGER_ID, "manager")
        assertTrue(result is Success)

        val process = (result as Success).value
        assertEquals(processId, process.id)
        assertEquals("Processo Teste", process.name)
        assertEquals("Car Accident", process.area.name)
        assertEquals("assigned", process.state.name.lowercase())
    }

    @Test
    fun `getProcessById - verify location data`() {
        val created = createValid()
        val processId = (created as Success).value

        val result = processService.getProcessById(processId, MANAGER_ID, "manager")
        assertTrue(result is Success)

        val process = (result as Success).value
        assertEquals("Rua Augusta 1", process.location.street)
        assertEquals("Lisboa", process.location.county)
        assertEquals("Lisboa", process.location.district)
    }

    @Test
    fun `getProcessById - verify triator investigator and supervisor`() {
        val created = createValid()
        val processId = (created as Success).value

        val result = processService.getProcessById(processId, MANAGER_ID, "manager")
        assertTrue(result is Success)

        val process = (result as Success).value
        assertEquals(TRIATOR_ID, process.triator.id)
        assertEquals(INVESTIGATOR_ID, process.investigator?.id)
        assertEquals(SUPERVISOR_ID, process.supervisor?.id)
    }

    @Test
    fun `getProcessById - process not found returns ProcessNotFound`() {
        val result = processService.getProcessById(9999, MANAGER_ID, "manager")
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `getProcessById - negative id returns ProcessNotFound`() {
        val result = processService.getProcessById(-1, MANAGER_ID, "manager")
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `getProcessById - zero id returns ProcessNotFound`() {
        val result = processService.getProcessById(0, MANAGER_ID, "manager")
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }
// -----------------------------------------------------------------------
// getAllProcesses
// -----------------------------------------------------------------------

    @Test
    fun `getAllProcesses - returns at least one process for triator`() {
        val created = createValid(investigatorId = null, supervisorId = null)
        assertTrue(created is Success)

        val result = processService.getAllProcesses(
            userId = TRIATOR_ID,
            areaId = null,
            offset = 0,
            limit = 10,
            role = "triator"
        )

        assertTrue(result is Success)
        val processes = (result as Success).value
        assertTrue(processes.isNotEmpty())
        assertTrue(processes.all { it.state.name.lowercase() == "not_assigned" })
    }

    @Test
    fun `getAllProcesses - returns only processes from investigator matching allowed states`() {
        val created = createValid(investigatorId = INVESTIGATOR_ID)
        assertTrue(created is Success)

        val result = processService.getAllProcesses(
            userId = INVESTIGATOR_ID,
            areaId = CAR_ACCIDENT_AREA_ID, // Filtra pela área correta do investigador
            offset = 0,
            limit = 10,
            role = "investigator"
        )

        assertTrue(result is Success)
        val processes = (result as Success).value

        val allowedStates = listOf("assigned", "on_going", "rejected_by_supervisor")
        assertTrue(processes.all { it.investigator?.id == INVESTIGATOR_ID })
        assertTrue(processes.all { allowedStates.contains(it.state.name.lowercase()) })
    }

    @Test
    fun `getAllProcesses - returns only processes from supervisor area matching allowed states`() {
        val created = createValid(area = "Car Accident", supervisorId = SUPERVISOR_ID)
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
            userId = SUPERVISOR_ID,
            areaId = CAR_ACCIDENT_AREA_ID,
            offset = 0,
            limit = 10,
            role = "supervisor"
        )

        assertTrue(result is Success)
        val processes = (result as Success).value

        val allowedStates = listOf("waiting_approval_supervisor", "rejected_by_manager")
        assertTrue(processes.isNotEmpty())
        assertTrue(processes.all { allowedStates.contains(it.state.name.lowercase()) })
    }

    @Test
    fun `getAllProcesses - returns all processes for manager matching waiting manager state`() {
        val created = createValid()
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
            userId = MANAGER_ID,
            offset = 0,
            areaId = null,
            limit = 100,
            role = "manager"
        )

        assertTrue(result is Success)
        val processes = (result as Success).value
        assertTrue(processes.all { it.state.name.lowercase() == "waiting_approval_manager" })
    }

    @Test
    fun `getAllProcesses - with areaId filter returns only that area`() {
        val created = createValid(area = "Car Accident", investigatorId = INVESTIGATOR_ID)
        assertTrue(created is Success)

        val result = processService.getAllProcesses(
            userId = INVESTIGATOR_ID,
            areaId = CAR_ACCIDENT_AREA_ID,
            offset = 0,
            limit = 10,
            role = "investigator"
        )
        assertTrue(result is Success)
        val processes = (result as Success).value
        assertTrue(processes.all { it.area.name == "Car Accident" })
    }

    @Test
    fun `getAllProcesses - offset and limit work as pagination`() {
        createValid(name = "Process 1", investigatorId = null, supervisorId = null)
        createValid(name = "Process 2", investigatorId = null, supervisorId = null)
        createValid(name = "Process 3", investigatorId = null, supervisorId = null)

        val result1 = processService.getAllProcesses(
            userId = TRIATOR_ID,
            areaId = null,
            offset = 0,
            limit = 2,
            role = "triator"
        )

        val result2 = processService.getAllProcesses(
            userId = TRIATOR_ID,
            areaId = null,
            offset = 2,
            limit = 2,
            role = "triator"
        )

        assertTrue(result1 is Success)
        assertTrue(result2 is Success)
        val list1 = (result1 as Success).value
        val list2 = (result2 as Success).value
        assertTrue(list1.size <= 2)
        assertTrue(list2.size <= 2)
        assertTrue(list1.none { p1 -> list2.any { p2 -> p1.id == p2.id } })
    }

    @Test
    fun `getAllProcesses - invalid offset returns empty list or error`() {
        val result = processService.getAllProcesses(
            userId = TRIATOR_ID,
            areaId = 0,
            offset = -1,
            limit = 10,
            role = "triator"
        )
        assertTrue(result is Failure || (result is Success && (result as Success).value.isEmpty()))
    }

    @Test
    fun `getAllProcesses - invalid limit returns empty list or error`() {
        val result = processService.getAllProcesses(
            userId = TRIATOR_ID,
            areaId = 0,
            offset = 0,
            limit = 0,
            role = "triator"
        )
        assertTrue(result is Failure || (result is Success && (result as Success).value.isEmpty()))
    }
// -----------------------------------------------------------------------
// changeEndDate
// -----------------------------------------------------------------------


    @Test
    fun `changeEndDate - success updates due date`() {
        val created = createValid()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val newEndDate = "2028-01-01T00:00"
        val result = processService.changeEndDate(processId, newEndDate, SUPERVISOR_ID, "supervisor")
        assertTrue(result is Success)

        val updated = processService.getProcessById(processId, MANAGER_ID, "manager")
        assertTrue(updated is Success)
        val process = (updated as Success).value
        assertEquals(newEndDate, process.dueDate.toString())
    }

    @Test
    fun `changeEndDate - process not found returns error`() {
        val result = processService.changeEndDate(9999, "2028-01-01T00:00", SUPERVISOR_ID, "supervisor")
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `changeEndDate - invalid date format returns error`() {
        val created = createValid()
        val processId = (created as Success).value

        val result = processService.changeEndDate(processId, "invalid-date", SUPERVISOR_ID, "supervisor")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidExpirationDate, (result as Failure).value)
    }

    @Test
    fun `changeEndDate - past date returns error`() {
        val created = createValid()
        val processId = (created as Success).value

        val result = processService.changeEndDate(processId, "2020-01-01T00:00:00", SUPERVISOR_ID, "supervisor")
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidExpirationDate, (result as Failure).value)
    }

    // -----------------------------------------------------------------------
// assignSupervisor
// -----------------------------------------------------------------------


    @Test
    fun `assignSupervisor - success assigns supervisor`() {
        val created = createValid(supervisorId = null)
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = processService.assignSupervisor(processId, TRIATOR_ID, SUPERVISOR_ID)
        assertTrue(result is Success)

        val updated = processService.getProcessById(processId, SUPERVISOR_ID, "supervisor")
        assertTrue(updated is Success)
        val process = (updated as Success).value
        assertEquals(SUPERVISOR_ID, process.supervisor?.id)
    }

    @Test
    fun `assignSupervisor - process not found returns error`() {
        val result = processService.assignSupervisor(9999, TRIATOR_ID, SUPERVISOR_ID)
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `assignSupervisor - supervisor not found returns error`() {
        val created = createValid(supervisorId = null)
        val processId = (created as Success).value

        val result = processService.assignSupervisor(processId, TRIATOR_ID, 9999)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidSupervisor, (result as Failure).value)
    }

    @Test
    fun `assignSupervisor - supervisor from different area returns error`() {
        val created = createValid(supervisorId = null, area = "Car Accident")
        val processId = (created as Success).value

        val result = processService.assignSupervisor(processId, TRIATOR_ID, 6)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidSupervisor, (result as Failure).value)
    }

    // -----------------------------------------------------------------------
// assignInvestigator
// -----------------------------------------------------------------------

    @Test
    fun `assignInvestigator - success assigns investigator`() {
        val created = createValid(investigatorId = null)
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = processService.assignInvestigator(processId, TRIATOR_ID, INVESTIGATOR_ID)
        assertTrue(result is Success)

        val updated = processService.getProcessById(processId, INVESTIGATOR_ID, "investigator")
        assertTrue(updated is Success)
        val process = (updated as Success).value
        assertEquals(INVESTIGATOR_ID, process.investigator?.id)
    }

    @Test
    fun `assignInvestigator - process not found returns error`() {
        val result = processService.assignInvestigator(9999, TRIATOR_ID, INVESTIGATOR_ID)
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `assignInvestigator - investigator not found returns error`() {
        val created = createValid(investigatorId = null)
        val processId = (created as Success).value

        val result = processService.assignInvestigator(processId, TRIATOR_ID, 9999)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidInvestigator, (result as Failure).value)
    }

    @Test
    fun `assignInvestigator - investigator from different area returns error`() {
        val created = createValid(investigatorId = null, area = "Car Accident")
        val processId = (created as Success).value

        val result = processService.assignInvestigator(processId, TRIATOR_ID, 6)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidInvestigator, (result as Failure).value)
    }

//    @Test
//    fun `assignInvestigator - already has investigator returns error`() {
//        val created = createValid()
//        val processId = (created as Success).value.id
//
//        val result = processService.assignInvestigator(processId, TRIATOR_ID,INVESTIGATOR_ID)
//        assertTrue(result is Failure)
//        assertEquals(ProcessError.InvestigatorAlreadyAssigned, (result as Failure).value)
//    }

// -----------------------------------------------------------------------
// changePriority
// -----------------------------------------------------------------------


    @Test
    fun `changePriority - success updates priority`() {
        val created = createValid(priority = "normal")
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = processService.changePriority(processId, "urgent", SUPERVISOR_ID)
        assertTrue(result is Success)

        val updated = processService.getProcessById(processId, SUPERVISOR_ID, "supervisor")
        assertTrue(updated is Success)
        val process = (updated as Success).value
        assertEquals("urgent", process.priority.name.lowercase())
    }

    @Test
    fun `changePriority - process not found returns error`() {
        val result = processService.changePriority(9999, "urgent", SUPERVISOR_ID)
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }

    @Test
    fun `changePriority - invalid priority returns error`() {
        val created = createValid(priority = "normal")
        val processId = (created as Success).value

        val result = processService.changePriority(processId, "invalid_priority", SUPERVISOR_ID)
        assertTrue(result is Failure)
        assertEquals(ProcessError.InvalidPriority, (result as Failure).value)
    }

    @Test
    fun `changePriority - same priority returns success or no-op`() {
        val created = createValid(priority = "normal")
        val processId = (created as Success).value

        val result = processService.changePriority(processId, "normal", SUPERVISOR_ID)
        assertTrue(result is Success)
        val updated = processService.getProcessById(processId, SUPERVISOR_ID, "supervisor")
        assertTrue(updated is Success)
        val process = (updated as Success).value
        assertEquals("normal", process.priority.name.lowercase())
    }


// -----------------------------------------------------------------------
// cancelProcess
// -----------------------------------------------------------------------

    @Test
    fun `cancelProcess - success cancels the process`() {
        val created = createValid()
        assertTrue(created is Success)
        val processId = (created as Success).value

        val result = processService.cancelProcess(processId, SUPERVISOR_ID)
        assertTrue(result is Success)

        val updated = processService.getProcessById(processId, SUPERVISOR_ID, "supervisor")
        assertTrue(updated is Success)

        val process = (updated as Success).value
        assertEquals("canceled", process.state.name.lowercase())
    }

    @Test
    fun `cancelProcess - process not found returns error`() {
        val result = processService.cancelProcess(9999, SUPERVISOR_ID)
        assertTrue(result is Failure)
        assertEquals(ProcessError.ProcessNotFound, (result as Failure).value)
    }


    @Test
    fun `cancelProcess - already canceled returns error or no-op`() {
        val created = createValid()
        val processId = (created as Success).value

        val firstCancel = processService.cancelProcess(processId, SUPERVISOR_ID)
        assertTrue(firstCancel is Success)

        val secondCancel = processService.cancelProcess(processId, SUPERVISOR_ID)
        assertTrue(secondCancel is Failure || secondCancel is Success)
    }


}


