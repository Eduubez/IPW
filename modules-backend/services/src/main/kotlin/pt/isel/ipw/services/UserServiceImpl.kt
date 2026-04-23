package pt.isel.ipw.services

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pt.isel.ipw.domain.User
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.repository.Transaction
import pt.isel.ipw.repository.TransactionManager
import pt.isel.ipw.services.auth.LoginResult
import pt.isel.ipw.services.auth.RefreshAccessToken
import pt.isel.ipw.services.auth.SelectRoleResult
import pt.isel.ipw.services.auth.TokenService
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.UserError
import pt.isel.ipw.services.errors.failure
import pt.isel.ipw.services.errors.success
import pt.isel.ipw.services.interfaces.UserService
import java.time.Instant

@Service
class UserServiceImpl(
    private val transactionManager: TransactionManager,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService
) : UserService {

    private val validRoles = Roles.ALL.map { it.lowercase() }.toSet()

    override fun login(
        email: String,
        password: String
    ): Either<UserError, LoginResult> = transactionManager.run {
        val user: User? = usersRepository.getUserByEmail(email)

        val error: UserError? = validateLogin(user, password)
        error?.let { return@run failure(it) }

        val validUser = user!!
        val roles = usersRepository.getUserRoles(validUser.id)

        // 1 user = 1 active session
        accessTokensRepository.deleteByUserId(validUser.id)
        refreshTokensRepository.deleteByUserId(validUser.id)
        loginTokensRepository.deleteByUserId(validUser.id)

        val createdLoginToken = tokenService.createLoginToken(validUser.id)

        loginTokensRepository.create(
            token = createdLoginToken.token,
            userId = validUser.id,
            expiresAt = createdLoginToken.expiresAt
        )

        success(
            LoginResult(
                loginToken = createdLoginToken.token,
                userId = validUser.id,
                roles = roles,
                expiresAt = createdLoginToken.expiresAt
            )
        )
    }

    override fun refreshAccessToken(
        refreshToken: String
    ): Either<UserError, RefreshAccessToken> = transactionManager.run {
        val refreshClaims = try {
            tokenService.parseRefreshToken(refreshToken)
        } catch (e: Exception) {
            return@run failure(UserError.InvalidToken)
        }

        val storedRefreshToken = refreshTokensRepository.getByToken(refreshToken)
            ?: return@run failure(UserError.RefreshTokenNotFound)

        if (storedRefreshToken.userId != refreshClaims.userId ||
            storedRefreshToken.role != refreshClaims.role) {
            return@run failure(UserError.InvalidToken)
        }

        if (storedRefreshToken.expiresAt.isBefore(Instant.now())) {
            refreshTokensRepository.deleteByToken(refreshToken)
            accessTokensRepository.deleteByUserId(storedRefreshToken.userId)
            return@run failure(UserError.ExpiredRefreshToken)
        }

        accessTokensRepository.deleteByUserId(storedRefreshToken.userId)

        val createdAccessToken = tokenService.createAccessToken(
            userId = storedRefreshToken.userId,
            role = storedRefreshToken.role
        )

        accessTokensRepository.create(
            token = createdAccessToken.token,
            userId = storedRefreshToken.userId,
            role = storedRefreshToken.role,
            expiresAt = createdAccessToken.expiresAt
        )

        success(
            RefreshAccessToken(
                token = createdAccessToken.token,
                expiresAt = createdAccessToken.expiresAt
            )
        )
    }

    override fun getUserRoles(
        email: String
    ): Either<UserError, List<String>> = transactionManager.run {
        val user = usersRepository.getUserByEmail(email)
            ?: return@run failure(UserError.UserNotFound)

        success(usersRepository.getUserRoles(user.id))
    }

    override fun createUser(
        name: String,
        email: String,
        password: String,
        areaId: Int?,
        roles: List<String>
    ): Either<UserError, Int> = transactionManager.run {

        val normalizedRoles = roles.map { it.lowercase() }

        val error: UserError? = validateUserCreation(email, password, areaId, normalizedRoles)
        error?.let { return@run failure(it) }

        // salt é gerado automaticamente
        val passwordHash = passwordEncoder.encode(password)!!

        val userId = usersRepository.createUser(
            name = name,
            email = email,
            passwordHash = passwordHash,
            areaId = areaId
        )

        normalizedRoles.forEach { role ->
            usersRepository.addUserRole(userId, role)
        }

        if (normalizedRoles.any { it.uppercase() == Roles.SUPERVISOR }) {
            areasRepository.updateBoss(areaId!!, userId)
        }

        success(userId)
    }

    override fun selectRole(
        loginToken: String,
        role: String
    ): Either<UserError, SelectRoleResult> = transactionManager.run {

        val userId = try {
            tokenService.parseLoginToken(loginToken)
        } catch (e: Exception) {
            loginTokensRepository.deleteByToken(loginToken)
            return@run failure(UserError.InvalidToken)
        }

        val storedLoginToken = loginTokensRepository.getByToken(loginToken)
            ?: return@run failure(UserError.InvalidToken)

        if (storedLoginToken.userId != userId) {
            return@run failure(UserError.InvalidToken)
        }

        if (storedLoginToken.expiresAt.isBefore(Instant.now())) {
            loginTokensRepository.deleteByToken(loginToken)
            return@run failure(UserError.ExpiredLoginToken)
        }

        val storedRoles = usersRepository.getUserRoles(userId)
        val normalizedRequestedRole = role.lowercase()

        if (normalizedRequestedRole !in storedRoles) {
            return@run failure(UserError.InvalidRoleSelection)
        }

        loginTokensRepository.deleteByToken(loginToken)

        accessTokensRepository.deleteByUserId(userId)
        refreshTokensRepository.deleteByUserId(userId)

        val createdAccessToken = tokenService.createAccessToken(userId, normalizedRequestedRole)
        val createdRefreshToken = tokenService.createRefreshToken(userId, normalizedRequestedRole)

        accessTokensRepository.create(
            token = createdAccessToken.token,
            userId = userId,
            role = normalizedRequestedRole,
            expiresAt = createdAccessToken.expiresAt
        )

        refreshTokensRepository.create(
            token = createdRefreshToken.token,
            userId = userId,
            role = normalizedRequestedRole,
            expiresAt = createdRefreshToken.expiresAt
        )

        success(
            SelectRoleResult(
                accessToken = createdAccessToken.token,
                accessTokenExpiresAt = createdAccessToken.expiresAt,
                refreshToken = createdRefreshToken.token,
                refreshTokenExpiresAt = createdRefreshToken.expiresAt,
                role = normalizedRequestedRole
            )
        )
    }

    private fun validateLogin(user: User?, password: String): UserError? {
        return when {
            user == null -> UserError.InvalidCredentials
            !user.isActive -> UserError.UserNotActive
            !passwordEncoder.matches(password, user.passwordHash) -> UserError.InvalidCredentials
            else -> null
        }
    }

    private fun Transaction.validateUserCreation(
        email: String,
        password: String,
        areaId: Int?,
        roles: List<String>
    ): UserError? {
        val hasAreaRole = roles.any { it.uppercase() in Roles.AREA_ROLES }
        val onlyArealessRoles = roles.all { it.uppercase() in Roles.AREALESS_ROLES }

        return when {
            usersRepository.isUserStoredByEmail(email) -> UserError.UserAlreadyExists
            password.length < 5 -> UserError.InsecurePassword
            roles.isEmpty() || roles.size > 5 || roles.any { it !in validRoles } -> UserError.InvalidRoles

            hasAreaRole && areaId == null -> UserError.AreaRequired
            areaId == null && !onlyArealessRoles -> UserError.AreaRequired

            areaId != null && !areasRepository.isAreaStoredById(areaId) -> UserError.AreaNotFound

            roles.any { it.uppercase() == Roles.SUPERVISOR } &&
                    areasRepository.hasBoss(areaId!!) -> UserError.AreaAlreadyHasSupervisor

            else -> null
        }
    }
}