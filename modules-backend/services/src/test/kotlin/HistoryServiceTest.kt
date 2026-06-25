import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.repository.jdbi.configureWithAppRequirements
import pt.isel.ipw.repository.jdbi.transaction.JdbiTransactionManager


import pt.isel.ipw.services.HistoryServiceImpl
import pt.isel.ipw.services.errors.Failure

import pt.isel.ipw.services.errors.HistoryError
import pt.isel.ipw.services.errors.Success
import kotlin.apply
import kotlin.test.BeforeTest

import kotlin.test.Test


class HistoryServiceTest {
    companion object {
        private val jdbi = DbConfig.getConnection()

        private val historyService = HistoryServiceImpl(
            JdbiTransactionManager(jdbi)
        )
    }

    @BeforeTest
    fun cleanUp() {
        jdbi.useHandle<Exception> { handle ->
            handle.execute("call public.sample_data()")
        }
    }


    @Test
    fun `user with bad id should return BadRequest`() {
        val userId = -1
        val result = historyService.getUserHistory(userId, Roles.TRIATOR)
        when (result) {
            is Failure -> {
                assert(result.value is HistoryError.InvalidUserId)
                assert(result.value.status == 400)
            }

            else -> {
                assert(false) { "Expected failure but got success" }
            }
        }
    }

    @Test
    fun `user with no history should return empty list`() {
        val userId = 9999
        val result = historyService.getUserHistory(userId, Roles.TRIATOR)
        when (result) {
            is Failure -> {
                assert(false) { "Expected success but got failure: ${result.value}" }
            }

            is Success -> {
                val userHistory = result.value
                assert(userHistory.userId == userId)
                assert(userHistory.process.isEmpty()) { "Expected empty processes list but got: ${userHistory.process.size} elements" }
            }
        }
    }

    @Test
    fun `user with history should return correct processes1`() {
        val userId = 2
        val result = historyService.getUserHistory(userId, Roles.TRIATOR)
        when (result) {
            is Failure -> {
                assert(false) { "Expected success but got failure: ${result.value}" }
            }

            is Success -> {
                val userHistory = result.value
                assert(userHistory.userId == userId)
                assert(userHistory.process.size == 10) { "Expected 10 processes but got: ${userHistory.process.size}" }
                assert(
                    userHistory.process.containsAll(
                        listOf(
                            1,
                            2,
                            3,
                            4,
                            5,
                            6,
                            7,
                            8,
                            9,
                            10
                        )
                    )
                ) { "Expected processes [1, 2,3,4,5,6,7,8,9,10] but got: ${userHistory.process}" }
            }
        }
    }

    @Test
    fun `user with history should return correct processes2`() {
        val userId = 3
        val result = historyService.getUserHistory(userId, Roles.INVESTIGATOR)
        when (result) {
            is Failure -> {
                assert(false) { "Expected success but got failure: ${result.value}" }
            }

            is Success -> {
                val userHistory = result.value
                assert(userHistory.userId == userId)
                assert(userHistory.process.size == 5) { "Expected 5 processes but got: ${userHistory.process.size}" }
                assert(
                    userHistory.process.containsAll(
                        listOf(
                            1,
                            2,
                            3,
                            4,
                            5
                        )
                    )
                ) { "Expected processes [1, 2,3,4,5] but got: ${userHistory.process}" }
            }
        }
    }

    @Test
    fun `get history by area should return all area processes`() {
        val userId = 1
        val areaId = 1
        val result = historyService.getAreaHistory(userId,areaId)
        when (result) {
            is Failure -> {
                assert(false) { "Expected success but got failure: ${result.value}" }
            }

            is Success -> {
                val areaHistory = result.value
                assert(areaHistory.areaId == areaId)
                assert(areaHistory.process.size == 6) { "Expected 6 processes but got: ${areaHistory.process.size}" }
                assert(
                    areaHistory.process.containsAll(
                        listOf(
                            1,
                            2,
                            3,
                            7,
                            10
                        )
                    )
                ) { "Expected processes [1, 2,3, 7,10] but got: ${areaHistory.process}" }
            }
        }
    }

    @Test
    fun `Get history by area with no processes should return empty list`() {
        val userId = 1
        val areaId = 4
        val result = historyService.getAreaHistory(userId,areaId)
        when (result) {
            is Failure -> {
                assert(false) { "Expected success but got failure: ${result.value}" }
            }

            is Success -> {
                val areaHistory = result.value
                assert(areaHistory.areaId == areaId)
                assert(areaHistory.process.isEmpty()) { "Expected empty processes list but got: ${areaHistory.process.size} elements" }
            }
        }
    }

    @Test
    fun `Get history by area with invalid id should return BadRequest`() {
        val userId = 1
        val areaId = -1
        val result = historyService.getAreaHistory(userId,areaId)
        when (result) {
            is Failure -> {
                assert(result.value is HistoryError.InvalidAreaId)
                assert(result.value.status == 400)
            }

            else -> {
                assert(false) { "Expected failure but got success" }
            }
        }
    }

    @Test
    fun `Get History with a valid ID but area doesnt exist should return 404 area not found`() {
        val usrId = 1
        val areaId = 9999
        val result = historyService.getAreaHistory(usrId,areaId)
        when (result) {
            is Failure -> {
                assert(result.value is HistoryError.AreaNotFound)
                assert(result.value.status == 404)
            }

            is Success -> {
                assert(false) { "Expected success but got failure: ${result.value}" }
            }
        }
    }
}
