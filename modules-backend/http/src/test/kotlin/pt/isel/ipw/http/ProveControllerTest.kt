package pt.isel.ipw.http

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import pt.isel.ipw.domain.DTO.input.prove.CreateProveRequest
import pt.isel.ipw.domain.DTO.input.prove.CreateProveUploadUrlRequest
import pt.isel.ipw.domain.DTO.output.ListResponse
import pt.isel.ipw.domain.DTO.output.prove.CreateProveResponse
import pt.isel.ipw.domain.DTO.output.prove.CreateProveUploadUrlResponse
import pt.isel.ipw.domain.DTO.output.prove.ProveAccessUrlResponse
import pt.isel.ipw.domain.DTO.output.prove.ProveResponse
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.http.controllers.ProveController
import pt.isel.ipw.http.errors.Problem
import kotlin.test.Test

@SpringJUnitConfig(TestConfig::class)
class ProveControllerTest {

    @Autowired
    private lateinit var proveController: ProveController

    @BeforeEach
    fun resetDatabase() {
        TestUtils.clear(DbConfig.getConnection())
    }

    @AfterEach
    fun clearSecurityContext() {
        TestUtils.clearSecurityContext()
    }

    @Test
    fun `createUploadUrl - success`() {
        TestUtils.setUpSecurityContext(TestUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)

        val resp = proveController.createUploadUrl(PROCESS_ID, validUploadUrlRequest())
        val body = resp.body as CreateProveUploadUrlResponse

        assertEquals(200, resp.statusCode.value())
        assertTrue(body.uploadUrl.startsWith("http://storage.test/upload/processes/$PROCESS_ID/"))
        assertTrue(body.storageKey.startsWith("processes/$PROCESS_ID/"))
        assertTrue(body.storageKey.endsWith("-photo.png"))
    }

    @Test
    fun `createUploadUrl - invalid content type`() {
        TestUtils.setUpSecurityContext(TestUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)

        val resp = proveController.createUploadUrl(
            PROCESS_ID,
            validUploadUrlRequest().copy(contentType = "application/x-msdownload")
        )
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-content-type", error.type)
    }

    @Test
    fun `createUploadUrl - unauthorized user`() {
        TestUtils.setUpSecurityContext(TestUtils.MANAGER_ID, Roles.MANAGER)

        val resp = proveController.createUploadUrl(PROCESS_ID, validUploadUrlRequest())
        val error = resp.body as Problem

        assertEquals(403, resp.statusCode.value())
        assertEquals("problems/unauthorized", error.type)
    }

    @Test
    fun `createProve - success creates activity and moves assigned process to on going`() {
        TestUtils.setUpSecurityContext(TestUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)

        val resp = proveController.createProve(PROCESS_ID, validCreateProveRequest())
        val body = resp.body as CreateProveResponse

        assertEquals(201, resp.statusCode.value())
        assertTrue(body.id > 0)
        assertEquals("on_going", currentProcessState(PROCESS_ID))
        assertTrue(activityExists(PROCESS_ID, "CREATED_PROVE"))
    }

    @Test
    fun `createProve - invalid storage key`() {
        TestUtils.setUpSecurityContext(TestUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)

        val resp = proveController.createProve(
            PROCESS_ID,
            validCreateProveRequest().copy(storageKey = "other-process/photo.png")
        )
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-storage-key", error.type)
    }

    @Test
    fun `createProve - storage object missing`() {
        TestUtils.setUpSecurityContext(TestUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)

        val resp = proveController.createProve(
            PROCESS_ID,
            validCreateProveRequest().copy(storageKey = "processes/$PROCESS_ID/missing-photo.png")
        )
        val error = resp.body as Problem

        assertEquals(500, resp.statusCode.value())
        assertEquals("problems/storage-error", error.type)
    }

    @Test
    fun `getProcessProves - success`() {
        TestUtils.setUpSecurityContext(TestUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        proveController.createProve(PROCESS_ID, validCreateProveRequest())

        val resp = proveController.getProcessProves(PROCESS_ID)
        val body = resp.body as ListResponse<*>
        val prove = body.results.first() as ProveResponse

        assertEquals(200, resp.statusCode.value())
        assertEquals(PROCESS_ID, prove.processId)
        assertEquals("photo.png", prove.fileName)
        assertEquals("image/png", prove.contentType)
        assertEquals(TestUtils.INVESTIGATOR_ID, prove.createdBy)
        assertEquals("Ana Averiguador", prove.authorName)
    }

    @Test
    fun `getProveAccessUrl - success`() {
        TestUtils.setUpSecurityContext(TestUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        val proveId = createProve()

        val resp = proveController.getProveAccessUrl(PROCESS_ID, proveId)
        val body = resp.body as ProveAccessUrlResponse

        assertEquals(200, resp.statusCode.value())
        assertEquals("http://storage.test/access/processes/$PROCESS_ID/photo.png", body.url)
        assertEquals("image/png", body.contentType)
        assertEquals("photo.png", body.fileName)
    }

    @Test
    fun `getProveAccessUrl - prove from another process is not found`() {
        TestUtils.setUpSecurityContext(TestUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        val proveId = createProve()

        val resp = proveController.getProveAccessUrl(OTHER_PROCESS_ID, proveId)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/prove-not-found", error.type)
    }

    @Test
    fun `deleteProve - success deletes metadata and creates activity`() {
        TestUtils.setUpSecurityContext(TestUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)
        val proveId = createProve()

        val resp = proveController.deleteProve(PROCESS_ID, proveId)

        assertEquals(204, resp.statusCode.value())
        assertFalse(proveExists(proveId))
        assertTrue(activityExists(PROCESS_ID, "DELETED_PROVE"))
    }

    @Test
    fun `deleteProve - prove not found`() {
        TestUtils.setUpSecurityContext(TestUtils.INVESTIGATOR_ID, Roles.INVESTIGATOR)

        val resp = proveController.deleteProve(PROCESS_ID, 9999)
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/prove-not-found", error.type)
    }

    private fun createProve(): Int {
        val resp = proveController.createProve(PROCESS_ID, validCreateProveRequest())
        return (resp.body as CreateProveResponse).id
    }

    private fun validUploadUrlRequest() = CreateProveUploadUrlRequest(
        fileName = "photo.png",
        contentType = "image/png",
        fileSize = 1024,
    )

    private fun validCreateProveRequest() = CreateProveRequest(
        fileName = "photo.png",
        contentType = "image/png",
        fileSize = 1024,
        storageKey = "processes/$PROCESS_ID/photo.png",
    )

    private fun currentProcessState(processId: Int): String =
        DbConfig.getConnection().withHandle<String, Exception> { handle ->
            handle.createQuery(
                """
                select s.name
                from Process_State ps
                join State s on s.id = ps.state_id
                where ps.process_id = :processId
                  and ps.end_date is null
                """
            )
                .bind("processId", processId)
                .mapTo(String::class.java)
                .one()
        }

    private fun activityExists(processId: Int, action: String): Boolean =
        DbConfig.getConnection().withHandle<Boolean, Exception> { handle ->
            handle.createQuery(
                """
                select count(*) > 0
                from Activity
                where process_id = :processId
                  and action = :action
                """
            )
                .bind("processId", processId)
                .bind("action", action)
                .mapTo(Boolean::class.java)
                .one()
        }

    private fun proveExists(proveId: Int): Boolean =
        DbConfig.getConnection().withHandle<Boolean, Exception> { handle ->
            handle.createQuery("select count(*) > 0 from Proves where id = :proveId")
                .bind("proveId", proveId)
                .mapTo(Boolean::class.java)
                .one()
        }

    companion object {
        private const val PROCESS_ID = 1
        private const val OTHER_PROCESS_ID = 2
    }
}
