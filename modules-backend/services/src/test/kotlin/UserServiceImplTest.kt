import TestEitherUtils.assertFailure
import TestEitherUtils.assertSuccess
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.ipw.repository.jdbi.configureWithAppRequirements
import pt.isel.ipw.repository.jdbi.transaction.JdbiTransactionManager
import pt.isel.ipw.services.UserServiceImpl
import pt.isel.ipw.services.auth.JwtTokenService
import pt.isel.ipw.services.errors.UserError
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class UserServiceImplTest {

    companion object {
        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setUrl("jdbc:postgresql://localhost:5434/ipw_test")
                user = "postgres"
                password = "1234"
            }
        ).configureWithAppRequirements()

        private const val JWT_SECRET =
            "1234567890123456789012345678901234567890123456789012345678901234"

        private const val LOGIN_TOKEN_TTL_MINUTES = 5L
        private const val ACCESS_TOKEN_TTL_MINUTES = 120L
        private const val REFRESH_TOKEN_TTL_MINUTES = 10080L

        private val tokenService = JwtTokenService(
            secret = JWT_SECRET,
            loginTokenTtlMinutes = LOGIN_TOKEN_TTL_MINUTES,
            accessTokenTtlMinutes = ACCESS_TOKEN_TTL_MINUTES,
            refreshTokenTtlMinutes = REFRESH_TOKEN_TTL_MINUTES
        )

        private val userService = UserServiceImpl(
            transactionManager = JdbiTransactionManager(jdbi),
            passwordEncoder = BCryptPasswordEncoder(),
            tokenService = tokenService
        )
    }

    @BeforeTest
    fun cleanUp() {
        jdbi.useHandle<Exception> { handle ->
            handle.execute("call public.sample_data()")
        }
    }

    @Test
    fun `login with valid credentials should return login result`() {
        val createdUserId = createTestUser()

        val loginResult = assertSuccess(
            userService.login(
                email = "chico@gmail.com",
                password = "12345"
            )
        )

        assertTrue(loginResult.loginToken.isNotBlank())
        assertEquals(createdUserId, loginResult.userId)
        assertEquals(listOf("admin"), loginResult.roles)
    }

    @Test
    fun `login with wrong password should return invalid credentials`() {
        createTestUser()

        val error = assertFailure(
            userService.login(
                email = "chico@gmail.com",
                password = "wrong-password"
            )
        )

        assertIs<UserError.InvalidCredentials>(error)
    }

    @Test
    fun `login with unknown email should return invalid credentials`() {
        val error = assertFailure(
            userService.login(
                email = "unknown@gmail.com",
                password = "12345"
            )
        )

        assertIs<UserError.InvalidCredentials>(error)
    }

    @Test
    fun `select role with valid login token should return access and refresh tokens`() {
        val createdUserId = createTestUser()

        val loginResult = assertSuccess(
            userService.login(
                email = "chico@gmail.com",
                password = "12345"
            )
        )

        val selectRoleResult = assertSuccess(
            userService.selectRole(
                loginToken = loginResult.loginToken,
                userId = loginResult.userId,
                selectedRole = "admin"
            )
        )

        assertEquals(createdUserId, loginResult.userId)
        assertEquals("admin", selectRoleResult.role)
        assertTrue(selectRoleResult.accessToken.isNotBlank())
        assertTrue(selectRoleResult.refreshToken.isNotBlank())
    }

    @Test
    fun `select role with invalid role should return invalid role selection`() {
        createTestUser()

        val loginResult = assertSuccess(
            userService.login(
                email = "chico@gmail.com",
                password = "12345"
            )
        )

        val error = assertFailure(
            userService.selectRole(
                loginToken = loginResult.loginToken,
                userId = loginResult.userId,
                selectedRole = "manager"
            )
        )

        assertIs<UserError.InvalidRoleSelection>(error)
    }

    @Test
    fun `select role with unknown login token should return invalid token`() {
        val createdUserId = createTestUser()

        val error = assertFailure(
            userService.selectRole(
                loginToken = "unknown-token",
                userId = createdUserId,
                selectedRole = "admin"
            )
        )

        assertIs<UserError.InvalidToken>(error)
    }

    @Test
    fun `refresh access token with valid refresh token should return new access token`() {
        createTestUser()

        val loginResult = assertSuccess(
            userService.login(
                email = "chico@gmail.com",
                password = "12345"
            )
        )

        val selectRoleResult = assertSuccess(
            userService.selectRole(
                loginToken = loginResult.loginToken,
                userId = loginResult.userId,
                selectedRole = "admin"
            )
        )

        val refreshResult = assertSuccess(
            userService.refreshAccessToken(
                refreshToken = selectRoleResult.refreshToken,
                userId = loginResult.userId,
                role = "admin"
            )
        )

        assertTrue(refreshResult.token.isNotBlank())
    }

    @Test
    fun `refresh access token with unknown refresh token should return refresh token not found`() {
        val createdUserId = createTestUser()

        val error = assertFailure(
            userService.refreshAccessToken(
                refreshToken = "unknown-token",
                userId = createdUserId,
                role = "admin"
            )
        )

        assertIs<UserError.RefreshTokenNotFound>(error)
    }

    @Test
    fun `refresh access token with wrong role should return invalid token`() {
        createTestUser()

        val loginResult = assertSuccess(
            userService.login(
                email = "chico@gmail.com",
                password = "12345"
            )
        )

        val selectRoleResult = assertSuccess(
            userService.selectRole(
                loginToken = loginResult.loginToken,
                userId = loginResult.userId,
                selectedRole = "admin"
            )
        )

        val error = assertFailure(
            userService.refreshAccessToken(
                refreshToken = selectRoleResult.refreshToken,
                userId = loginResult.userId,
                role = "manager"
            )
        )

        assertIs<UserError.InvalidToken>(error)
    }

    @Test
    fun `get all users with valid pagination should return users`() {
        val createdUserId = createTestUser()

        val users = assertSuccess(
            userService.getAllUsers(
                offset = 0,
                limit = 100
            )
        )

        val createdUser = users.first { it.id == createdUserId }

        assertEquals("Chico", createdUser.name)
        assertEquals("chico@gmail.com", createdUser.email)
        assertEquals(listOf("admin"), createdUser.roles)
    }

    @Test
    fun `get all users with invalid offset should return invalid offset`() {
        val error = assertFailure(
            userService.getAllUsers(
                offset = -1,
                limit = 10
            )
        )

        assertIs<UserError.InvalidOffset>(error)
    }

    @Test
    fun `get all users with invalid limit should return invalid limit`() {
        val error = assertFailure(
            userService.getAllUsers(
                offset = 0,
                limit = 0
            )
        )

        assertIs<UserError.InvalidLimit>(error)
    }

    @Test
    fun `change user roles with valid roles should replace old roles`() {
        val createdUserId = createTestUser()

        assertSuccess(
            userService.changeUserRoles(
                userId = createdUserId,
                roles = listOf("admin", "triator"),
                areaId = null
            )
        )

        val roles = assertSuccess(
            userService.getUserRoles("chico@gmail.com")
        )

        assertEquals(setOf("admin", "triator"), roles.toSet())
    }

    @Test
    fun `change user roles with unknown user should return user not found`() {
        val error = assertFailure(
            userService.changeUserRoles(
                userId = -1,
                roles = listOf("admin"),
                areaId = null
            )
        )

        assertIs<UserError.UserNotFound>(error)
    }

    @Test
    fun `change user roles with invalid roles should return invalid roles`() {
        val createdUserId = createTestUser()

        val error = assertFailure(
            userService.changeUserRoles(
                userId = createdUserId,
                roles = listOf("not-a-role"),
                areaId = null
            )
        )

        assertIs<UserError.InvalidRoles>(error)
    }

    @Test
    fun `change user password with valid password should update password`() {
        val createdUserId = createTestUser()

        assertSuccess(
            userService.changeUserPassword(
                userId = createdUserId,
                newPassword = "new-password"
            )
        )

        val oldPasswordError = assertFailure(
            userService.login(
                email = "chico@gmail.com",
                password = "12345"
            )
        )

        val loginResult = assertSuccess(
            userService.login(
                email = "chico@gmail.com",
                password = "new-password"
            )
        )

        assertIs<UserError.InvalidCredentials>(oldPasswordError)
        assertEquals(createdUserId, loginResult.userId)
    }

    @Test
    fun `change user password with unknown user should return user not found`() {
        val error = assertFailure(
            userService.changeUserPassword(
                userId = -1,
                newPassword = "new-password"
            )
        )

        assertIs<UserError.UserNotFound>(error)
    }

    @Test
    fun `change user password with insecure password should return insecure password`() {
        val createdUserId = createTestUser()

        val error = assertFailure(
            userService.changeUserPassword(
                userId = createdUserId,
                newPassword = "1234"
            )
        )

        assertIs<UserError.InsecurePassword>(error)
    }

    private fun createTestUser(): Int =
        assertSuccess(
            userService.createUser(
                name = "Chico",
                email = "chico@gmail.com",
                password = "12345",
                areaId = null,
                roles = listOf("admin")
            )
        )
}
