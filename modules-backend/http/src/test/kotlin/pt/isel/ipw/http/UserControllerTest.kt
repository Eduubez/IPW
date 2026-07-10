package pt.isel.ipw.http

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import pt.isel.ipw.domain.DTO.input.ChangeUserPasswordRequest
import pt.isel.ipw.domain.DTO.input.ChangeUserRolesRequest
import pt.isel.ipw.domain.DTO.input.ChangeUserStatusRequest
import pt.isel.ipw.domain.DTO.input.CreateUserRequest
import pt.isel.ipw.domain.DTO.input.LoginRequest
import pt.isel.ipw.domain.DTO.input.SelectRoleRequest
import pt.isel.ipw.domain.DTO.output.AssignableUserResponse
import pt.isel.ipw.domain.DTO.output.CreateUserResponse
import pt.isel.ipw.domain.DTO.output.ListResponse
import pt.isel.ipw.domain.DTO.output.LoginResponse
import pt.isel.ipw.domain.DTO.output.RefreshTokenResponse
import pt.isel.ipw.domain.DTO.output.SelectRoleResponse
import pt.isel.ipw.domain.DTO.output.user.AdminUserResponse
import pt.isel.ipw.domain.DTO.output.user.UserProfileResponse
import pt.isel.ipw.domain.DTO.output.user.UserRolesResponse
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.http.auth.LoginTokenPrincipal
import pt.isel.ipw.http.auth.RefreshTokenPrincipal
import pt.isel.ipw.http.controllers.UserController
import pt.isel.ipw.http.errors.ErrorCode
import pt.isel.ipw.http.errors.Problem
import pt.isel.ipw.services.auth.LoginTokenClaims
import pt.isel.ipw.services.auth.TokenClaims
import kotlin.test.Test

@SpringJUnitConfig(TestConfig::class)
class UserControllerTest {

    @Autowired
    private lateinit var userController: UserController

    @BeforeEach
    fun resetDatabase() {
        TestUtils.clear(DbConfig.getConnection())
    }

    @AfterEach
    fun clearSecurityContext() {
        TestUtils.clearSecurityContext()
    }

    // ── createUser ───────────────────────────────────────────────────────────

    @Test
    fun `createUser - success`() {
        val req = validCreateUserRequest()

        val resp = userController.createUser(req)
        val body = resp.body as CreateUserResponse

        assertEquals(201, resp.statusCode.value())
        assertNotNull(body.id)
        assertEquals(req.name, body.name)
        assertEquals(req.email, body.email)
        assertEquals(req.roles, body.roles)
    }

    @Test
    fun `createUser - user already exists`() {
        val req = validCreateUserRequest()
        userController.createUser(req)

        val resp = userController.createUser(req)
        val error = resp.body as Problem

        assertEquals(409, resp.statusCode.value())
        assertEquals("problems/user-already-exists", error.type)
        assertEquals(ErrorCode.USER_ALREADY_EXISTS, error.errorCode)
    }

    @Test
    fun `createUser - insecure password`() {
        val resp = userController.createUser(validCreateUserRequest().copy(password = "1234"))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/insecure-password", error.type)
        assertEquals(ErrorCode.INSECURE_PASSWORD, error.errorCode)
    }

    @Test
    fun `createUser - invalid role`() {
        val resp = userController.createUser(validCreateUserRequest().copy(roles = listOf("not-a-role")))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-roles", error.type)
        assertEquals(ErrorCode.INVALID_ROLES, error.errorCode)
    }

    @Test
    fun `createUser - area required for investigator`() {
        val resp = userController.createUser(validCreateUserRequest().copy(roles = listOf(Roles.INVESTIGATOR), areaId = null))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/area-required", error.type)
        assertEquals(ErrorCode.AREA_REQUIRED, error.errorCode)
    }

    // ── login and role selection ──────────────────────────────────────────────

    @Test
    fun `login - success`() {
        createTestUser()

        val resp = userController.login(LoginRequest(email = "chico@gmail.com", password = "12345"))
        val body = resp.body as LoginResponse

        assertEquals(200, resp.statusCode.value())
        assertTrue(body.loginToken.value.isNotBlank())
        assertEquals(listOf(Roles.ADMIN), body.roles)
    }

    @Test
    fun `login - invalid credentials`() {
        val resp = userController.login(LoginRequest(email = "bob@ipw.pt", password = "wrong-password"))
        val error = resp.body as Problem

        assertEquals(401, resp.statusCode.value())
        assertEquals("problems/invalid-credentials", error.type)
        assertEquals(ErrorCode.INVALID_CREDENTIALS, error.errorCode)
    }

    @Test
    fun `roles - success`() {
        createTestUser()

        val resp = userController.roles("chico@gmail.com")
        val body = resp.body as UserRolesResponse

        assertEquals(200, resp.statusCode.value())
        assertEquals(listOf(Roles.ADMIN), body.roles)
    }

    @Test
    fun `roles - user not found`() {
        val resp = userController.roles("unknown@ipw.pt")
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/user-not-found", error.type)
        assertEquals(ErrorCode.USER_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `selectRole - success includes area info for investigator`() {
        createInvestigatorUser()

        val loginResp = userController.login(LoginRequest(email = "ivo@gmail.com", password = "12345"))
        val loginBody = loginResp.body as LoginResponse
        val principal = LoginTokenPrincipal(
            token = loginBody.loginToken.value,
            claims = LoginTokenClaims(userId = loginBody.userId, roles = loginBody.roles)
        )

        val resp = userController.selectRole(SelectRoleRequest(Roles.INVESTIGATOR), principal)
        val body = resp.body as SelectRoleResponse

        assertEquals(200, resp.statusCode.value())
        assertEquals(Roles.INVESTIGATOR, body.role)
        assertTrue(body.accessToken.value.isNotBlank())
        assertTrue(body.refreshToken.value.isNotBlank())
        assertNotNull(body.areaId)
        assertNotNull(body.area)
    }

    @Test
    fun `selectRole - invalid role selection`() {
        createInvestigatorUser()

        val loginResp = userController.login(LoginRequest(email = "ivo@gmail.com", password = "12345"))
        val loginBody = loginResp.body as LoginResponse
        val principal = LoginTokenPrincipal(
            token = loginBody.loginToken.value,
            claims = LoginTokenClaims(userId = loginBody.userId, roles = loginBody.roles)
        )

        val resp = userController.selectRole(SelectRoleRequest(Roles.ADMIN), principal)
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-role-selection", error.type)
        assertEquals(ErrorCode.INVALID_ROLE, error.errorCode)
    }

    @Test
    fun `refreshToken - success`() {
        createInvestigatorUser()

        val loginResp = userController.login(LoginRequest(email = "ivo@gmail.com", password = "12345"))
        val loginBody = loginResp.body as LoginResponse
        val selectResp = userController.selectRole(
            SelectRoleRequest(Roles.INVESTIGATOR),
            LoginTokenPrincipal(
                token = loginBody.loginToken.value,
                claims = LoginTokenClaims(userId = loginBody.userId, roles = loginBody.roles)
            )
        )
        val selectBody = selectResp.body as SelectRoleResponse

        val resp = userController.refreshToken(
            RefreshTokenPrincipal(
                token = selectBody.refreshToken.value,
                claims = TokenClaims(userId = loginBody.userId, role = Roles.INVESTIGATOR)
            )
        )
        val body = resp.body as RefreshTokenResponse

        assertEquals(200, resp.statusCode.value())
        assertTrue(body.token.value.isNotBlank())
    }

    // ── authenticated user endpoints ─────────────────────────────────────────

    @Test
    fun `getMe - success`() {
        val userId = createTestUser()
        TestUtils.setUpSecurityContext(userId = userId, role = Roles.ADMIN)

        val resp = userController.getMe()
        val body = resp.body as UserProfileResponse

        assertEquals(200, resp.statusCode.value())
        assertEquals(userId, body.id)
        assertEquals("Chico", body.name)
        assertEquals(listOf(Roles.ADMIN), body.roles)
    }

    @Test
    fun `getMe - missing authentication`() {
        assertThrows<AuthenticationCredentialsNotFoundException> {
            userController.getMe()
        }
    }

    @Test
    fun `logout - success`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.INVESTIGATOR_ID, role = Roles.INVESTIGATOR)

        val resp = userController.logout()

        assertEquals(200, resp.statusCode.value())
    }

    // ── admin user management ────────────────────────────────────────────────

    @Test
    fun `getAllUsers - success`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.ADMIN)

        val resp = userController.getAllUsers(offset = 0, limit = 10, name = "", areaId = null, isActive = null)
        val body = resp.body as ListResponse<*>

        assertEquals(200, resp.statusCode.value())
        assertTrue(body.results.isNotEmpty())
        assertNotNull(body.results.first() as? AdminUserResponse)
    }

    @Test
    fun `getAllUsers - invalid limit`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.ADMIN)

        val resp = userController.getAllUsers(offset = 0, limit = 0, name = "", areaId = null, isActive = null)
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/invalid-limit", error.type)
        assertEquals(ErrorCode.INVALID_LIMIT, error.errorCode)
    }

    @Test
    fun `changeUserRoles - success`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.ADMIN)
        val userId = createTestUser()

        val resp = userController.changeUserRoles(
            userId,
            ChangeUserRolesRequest(roles = listOf(Roles.ADMIN, Roles.TRIATOR), areaId = null)
        )

        assertEquals(200, resp.statusCode.value())
    }

    @Test
    fun `changeUserRoles - user not found`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.ADMIN)

        val resp = userController.changeUserRoles(
            9999,
            ChangeUserRolesRequest(roles = listOf(Roles.ADMIN), areaId = null)
        )
        val error = resp.body as Problem

        assertEquals(404, resp.statusCode.value())
        assertEquals("problems/user-not-found", error.type)
        assertEquals(ErrorCode.USER_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `changeUserPassword - success`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.ADMIN)
        val userId = createTestUser()

        val resp = userController.changeUserPassword(userId, ChangeUserPasswordRequest("new-password"))

        assertEquals(200, resp.statusCode.value())
    }

    @Test
    fun `changeUserPassword - insecure password`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.ADMIN)
        val userId = createTestUser()

        val resp = userController.changeUserPassword(userId, ChangeUserPasswordRequest("1234"))
        val error = resp.body as Problem

        assertEquals(400, resp.statusCode.value())
        assertEquals("problems/insecure-password", error.type)
        assertEquals(ErrorCode.INSECURE_PASSWORD, error.errorCode)
    }

    @Test
    fun `changeUserStatus - success`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.ADMIN)
        val userId = createTestUser()

        val resp = userController.changeUserStatus(userId, ChangeUserStatusRequest(isActive = false))

        assertEquals(204, resp.statusCode.value())
    }

    @Test
    fun `changeUserStatus - cannot deactivate self`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.ADMIN)

        val resp = userController.changeUserStatus(TestUtils.ADMIN_ID, ChangeUserStatusRequest(isActive = false))
        val error = resp.body as Problem

        assertEquals(409, resp.statusCode.value())
        assertEquals("problems/cannot-deactivate-self", error.type)
        assertEquals(ErrorCode.CANNOT_DEACTIVATE_SELF, error.errorCode)
    }

    @Test
    fun `changeUserStatus - missing authentication`() {
        assertThrows<AuthenticationCredentialsNotFoundException> {
            userController.changeUserStatus(TestUtils.INVESTIGATOR_ID, ChangeUserStatusRequest(isActive = false))
        }
    }

    // ── assignable users ─────────────────────────────────────────────────────

    @Test
    fun `getAllInvestigators - success`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = userController.getAllInvestigators(areaId = null)
        val body = resp.body as ListResponse<*>

        assertEquals(200, resp.statusCode.value())
        assertTrue(body.results.isNotEmpty())
        assertNotNull(body.results.first() as? AssignableUserResponse)
    }

    @Test
    fun `getAllSupervisors - success`() {
        TestUtils.setUpSecurityContext(userId = TestUtils.TRIATOR_ID, role = Roles.TRIATOR)

        val resp = userController.getAllSupervisors(areaId = null)
        val body = resp.body as ListResponse<*>

        assertEquals(200, resp.statusCode.value())
        assertTrue(body.results.isNotEmpty())
        assertNotNull(body.results.first() as? AssignableUserResponse)
    }

    private fun createTestUser(): Int {
        TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.ADMIN)
        val resp = userController.createUser(validCreateUserRequest())
        return (resp.body as CreateUserResponse).id
    }

    private fun createInvestigatorUser(): Int {
        TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.ADMIN)
        val resp = userController.createUser(
            CreateUserRequest(
                name = "Ivo",
                email = "ivo@gmail.com",
                password = "12345",
                areaId = 1,
                roles = listOf(Roles.INVESTIGATOR)
            )
        )
        return (resp.body as CreateUserResponse).id
    }

    private fun validCreateUserRequest(): CreateUserRequest {
        TestUtils.setUpSecurityContext(userId = TestUtils.ADMIN_ID, role = Roles.ADMIN)
        return CreateUserRequest(
            name = "Chico",
            email = "chico@gmail.com",
            password = "12345",
            areaId = null,
            roles = listOf(Roles.ADMIN)
        )
    }
}
