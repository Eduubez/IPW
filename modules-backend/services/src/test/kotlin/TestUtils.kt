import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.ipw.repository.jdbi.transaction.JdbiTransactionManager
import pt.isel.ipw.services.*
import pt.isel.ipw.services.auth.JwtTokenService
import pt.isel.ipw.services.errors.Success
import pt.isel.ipw.services.results.CreateProcessResult

class TestUtils(
    private val jdbi: Jdbi,
) {


    val TRIATOR_ID = 1
    val INVESTIGATOR_ID = 2
    val SUPERVISOR_ID = 3
    val MANAGER_ID = 4
    val CAR_ACCIDENT_AREA_ID = 1


    fun cleanRepo() {
        jdbi.useHandle<Exception> { handle ->
            handle.execute("call public.sample_data()")
        }
    }

    private val trxManager = JdbiTransactionManager(jdbi)


    val areaService = AreaServiceImpl(
        trxManager
    )

    val historyService = HistoryServiceImpl(
        trxManager
    )

    val activityService = ActivityServiceImpl(
        trxManager
    )

    val processService = ProcessServiceImpl(
        trxManager, activityService
    )

    val noteService = NoteServiceImpl(
        trxManager, activityService
    )

    val reportService = ReportServiceImpl(
        JdbiTransactionManager(jdbi),
        activityService

    )

    private val tokenService = JwtTokenService(
        secret = "1234567890123456789012345678901234567890123456789012345678901234",
        loginTokenTtlMinutes = 5L,
        accessTokenTtlMinutes = 120L,
        refreshTokenTtlMinutes = 10080L
    )

    val userService = UserServiceImpl(
    transactionManager = JdbiTransactionManager(jdbi),
    passwordEncoder = BCryptPasswordEncoder(),
    tokenService = tokenService
    )

    fun createProcess(
        userId: Int = TRIATOR_ID,
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



    fun createProve(processId: Int): Int =
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
}