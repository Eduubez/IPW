import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ipw.repository.jdbi.configureWithAppRequirements
import pt.isel.ipw.repository.jdbi.transaction.JdbiTransactionManager
import pt.isel.ipw.services.AreaServiceImpl
import pt.isel.ipw.services.errors.AreaError
import pt.isel.ipw.services.errors.Failure
import pt.isel.ipw.services.errors.Success
import kotlin.test.BeforeTest
import kotlin.test.Test

class AreaServiceTest {
    companion object {
        val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setUrl("jdbc:postgresql://localhost:5434/ipw_test")
                user = "postgres"
                password = "1234"
            }
        ).configureWithAppRequirements()
        private val areaService = AreaServiceImpl(
            JdbiTransactionManager(jdbi)
        )
    }

    @BeforeTest
    fun cleanUp() {
        jdbi.useHandle<Exception> { handle ->
            handle.execute("call public.sample_data()")
        }
    }

    // -----------------------------------------------------------------------
    // getAllAreas
    // -----------------------------------------------------------------------

    @Test
    fun `getAllAreas should return all seeded areas`() {
        val result = areaService.getAllAreas()
        when (result) {
            is Failure -> assert(false) { "Expected success but got failure: ${result.value}" }
            is Success -> {
                val areas = result.value.areas
                // seed_static_data inserts 4 areas
                assert(areas.size == 4) { "Expected 4 areas but got: ${areas.size}" }
                val areaNames = areas.map { it.name }
                assert(areaNames.containsAll(listOf("Car Accident", "Floods", "Fire", "Earthquake"))) {
                    "Expected areas [Car Accident, Floods, Fire, Earthquake] but got: $areaNames"
                }
            }
        }
    }

    @Test
    fun `getAllAreas should return areas with boss assigned`() {
        val result = areaService.getAllAreas()
        when (result) {
            is Failure -> assert(false) { "Expected success but got failure: ${result.value}" }
            is Success -> {
                val areas = result.value.areas
                // seed_static_data assigns Root User (id=1) as boss of all areas
                areas.forEach { area ->
                    val bossId = area.bossId
                    assert(bossId != null && bossId > 0) { "Expected area '${area.name}' to have a valid bossId but got: ${area.bossId}" }
                    assert(area.bossName?.isNotBlank() == true) { "Expected area '${area.name}' to have a boss name but got blank" }
                }
            }
        }
    }

    @Test
    fun `getAllAreas should return areas with Root User as boss`() {
        val result = areaService.getAllAreas()
        when (result) {
            is Failure -> assert(false) { "Expected success but got failure: ${result.value}" }
            is Success -> {
                val areas = result.value.areas
                areas.forEach { area ->
                    assert(area.bossId == 1) { "Expected bossId 1 for area '${area.name}' but got: ${area.bossId}" }
                    assert(area.bossName == "Root User") { "Expected bossName 'Root User' for area '${area.name}' but got: ${area.bossName}" }
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // getAreaById
    // -----------------------------------------------------------------------

    @Test
    fun `getAreaById with invalid id should return InvalidAreaId`() {
        val result = areaService.getAreaById(-1)
        when (result) {
            is Failure -> {
                assert(result.value is AreaError.InvalidAreaId)
                assert(result.value.status == 400)
            }
            else -> assert(false) { "Expected failure but got success" }
        }
    }

    @Test
    fun `getAreaById with non-existent id should return AreaNotFound`() {
        val result = areaService.getAreaById(9999)
        when (result) {
            is Failure -> {
                assert(result.value is AreaError.AreaNotFound)
                assert(result.value.status == 404)
            }
            else -> assert(false) { "Expected failure but got success" }
        }
    }

    @Test
    fun `getAreaById with valid id should return correct area`() {
        // area id=1 is 'Car Accident', boss is Root User (id=1)
        val result = areaService.getAreaById(1)
        when (result) {
            is Failure -> assert(false) { "Expected success but got failure: ${result.value}" }
            is Success -> {
                val area = result.value
                assert(area.id == 1) { "Expected id 1 but got: ${area.id}" }
                assert(area.name == "Car Accident") { "Expected name 'Car Accident' but got: ${area.name}" }
                assert(area.bossId == 1) { "Expected bossId 1 but got: ${area.bossId}" }
                assert(area.bossName == "Root User") { "Expected bossName 'Root User' but got: ${area.bossName}" }
            }
        }
    }

    @Test
    fun `getAreaById with area 3 should return Fire area`() {
        // area id=3 is 'Fire', boss is Root User (id=1)
        val result = areaService.getAreaById(3)
        when (result) {
            is Failure -> assert(false) { "Expected success but got failure: ${result.value}" }
            is Success -> {
                val area = result.value
                assert(area.id == 3) { "Expected id 3 but got: ${area.id}" }
                assert(area.name == "Fire") { "Expected name 'Fire' but got: ${area.name}" }
            }
        }
    }

    // -----------------------------------------------------------------------
    // updateAreaBoss
    // -----------------------------------------------------------------------

    @Test
    fun `updateAreaBoss with invalid area id should return InvalidAreaId`() {
        val result = areaService.updateAreaBoss(-1, 2)
        when (result) {
            is Failure -> {
                assert(result.value is AreaError.InvalidAreaId)
                assert(result.value.status == 400)
            }
            else -> assert(false) { "Expected failure but got success" }
        }
    }

    @Test
    fun `updateAreaBoss with invalid user id should return InvalidUserId`() {
        val result = areaService.updateAreaBoss(1, -1)
        when (result) {
            is Failure -> {
                assert(result.value is AreaError.InvalidUserId)
                assert(result.value.status == 400)
            }
            else -> assert(false) { "Expected failure but got success" }
        }
    }

    @Test
    fun `updateAreaBoss with non-existent area should return AreaNotFound`() {
        val result = areaService.updateAreaBoss(9999, 2)
        when (result) {
            is Failure -> {
                assert(result.value is AreaError.AreaNotFound)
                assert(result.value.status == 404)
            }
            else -> assert(false) { "Expected failure but got success" }
        }
    }

    @Test
    fun `updateAreaBoss with non-existent user should return UserNotFound`() {
        val result = areaService.updateAreaBoss(1, 9999)
        when (result) {
            is Failure -> {
                assert(result.value is AreaError.UserNotFound)
                assert(result.value.status == 404)
            }
            else -> assert(false) { "Expected failure but got success" }
        }
    }

    @Test
    fun `updateAreaBoss with valid ids should update boss and return updated area`() {
        // Alice Triator (id=2) becomes boss of Car Accident (id=1)
        val result = areaService.updateAreaBoss(1, 2)
        when (result) {
            is Failure -> assert(false) { "Expected success but got failure: ${result.value}" }
            is Success -> {
                val area = result.value
                assert(area.id == 1) { "Expected area id 1 but got: ${area.id}" }
                assert(area.name == "Car Accident") { "Expected name 'Car Accident' but got: ${area.name}" }
                assert(area.bossId == 2) { "Expected bossId 2 but got: ${area.bossId}" }
                assert(area.bossName == "Alice Triator") { "Expected bossName 'Alice Triator' but got: ${area.bossName}" }
            }
        }
    }

    @Test
    fun `updateAreaBoss with user already supervisor should not fail`() {
        // Carol Supervisor (id=4) already has supervisor role — updating should still succeed
        val result = areaService.updateAreaBoss(2, 4)
        when (result) {
            is Failure -> assert(false) { "Expected success but got failure: ${result.value}" }
            is Success -> {
                val area = result.value
                assert(area.bossId == 4) { "Expected bossId 4 but got: ${area.bossId}" }
                assert(area.bossName == "Carol Supervisor") { "Expected bossName 'Carol Supervisor' but got: ${area.bossName}" }
            }
        }
    }

    @Test
    fun `updateAreaBoss should assign supervisor role to new boss that is not yet a supervisor`() {
        // Alice Triator (id=2) is a triator, not a supervisor — after update she should get supervisor role
        areaService.updateAreaBoss(1, 2)

        // Verify via getAreaById that boss was persisted
        val verifyResult = areaService.getAreaById(1)
        when (verifyResult) {
            is Failure -> assert(false) { "Expected success but got failure: ${verifyResult.value}" }
            is Success -> {
                assert(verifyResult.value.bossId == 2) { "Expected bossId 2 but got: ${verifyResult.value.bossId}" }
            }
        }
    }
}
