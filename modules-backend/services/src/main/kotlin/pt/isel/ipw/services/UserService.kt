package pt.isel.ipw.services

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pt.isel.ipw.domain.User
import pt.isel.ipw.repository.Transaction
import pt.isel.ipw.repository.TransactionManager
import pt.isel.ipw.services.auth.LoginResult
import pt.isel.ipw.services.auth.RefreshAccessToken
import pt.isel.ipw.services.auth.TokenService
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.Success
import pt.isel.ipw.services.errors.UserError
import pt.isel.ipw.services.errors.failure
import pt.isel.ipw.services.errors.success


@Service
class UserService(
    private val transactionManager: TransactionManager,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService
) {

    private val validRoles = setOf("triator", "investigator", "supervisor", "manager", "admin")

    fun login(
        email: String,
        password: String
    ): Either<UserError, LoginResult> = transactionManager.run {
        val user: User? = usersRepository.getUserByEmail(email)

        val error: UserError? = validateLogin(user, password)
        error?.let { return@run  failure(it)}

        val validUser = user!!
        val roles = usersRepository.getUserRoles(validUser.id)

        // atençao aqui -> isto significa 1 user = 1 sessao ativa
        tokensRepository.deleteAccessTokens(validUser.id)
        refreshTokensRepository.deleteRefreshTokens(validUser.id)

        val createdAccessToken = tokenService.createAccessToken(validUser.id, roles)
        val createdRefreshToken = tokenService.createRefreshToken(validUser.id)

        tokensRepository.createToken(
            createdAccessToken.token,
            validUser.id,
            activeRole = null,
            expiresAt = createdAccessToken.expiresAt
        )

        refreshTokensRepository.createRefreshToken(
            createdRefreshToken.token,
            userId = validUser.id,
            expiresAt = createdRefreshToken.expiresAt
        )

        success(
            LoginResult(
                token = createdAccessToken.token,
                userId = validUser.id,
                roles = roles,
                expiresAt = createdAccessToken.expiresAt,
                refreshToken = createdRefreshToken.token,
                refreshExpiresAt = createdRefreshToken.expiresAt
            )
        )
    }

    fun refreshAccessToken(refreshToken: String): Either<UserError, RefreshAccessToken> = transactionManager.run {
        val error: UserError? = validateRefreshToken(refreshToken)
        if(error != null) return@run failure(error)

        val storedRefreshToken = refreshTokensRepository.getRefreshToken(refreshToken)!!
        val currentAccessToken = tokensRepository.getAccessToken(storedRefreshToken.userId)!!
        val roles = usersRepository.getUserRoles(storedRefreshToken.userId)

        val createdAccessToken = tokenService.createAccessToken(storedRefreshToken.userId, roles)

        tokensRepository.deleteAccessTokens(storedRefreshToken.userId)

        tokensRepository.createToken(
            createdAccessToken.token,
            storedRefreshToken.userId,
            currentAccessToken.activeRole,
            createdAccessToken.expiresAt
        )

        success(
            RefreshAccessToken(
                token = createdAccessToken.token,
                expiresAt = createdAccessToken.expiresAt
            )
        )
    }

    fun getUserRoles(
        email: String
    ): Either<UserError, List<String>> = transactionManager.run {
        val user = usersRepository.getUserByEmail(email)
            ?: return@run failure(UserError.UserNotFound)

        success(usersRepository.getUserRoles(user.id))
    }

    fun createUser(
        name: String,
        email: String,
        password: String,
        areaId: Int?,
        roles: List<String>
    ): Either<UserError, Int> = transactionManager.run {

        val error: UserError? = validateUserCreation(email, password, roles)
        error?.let { return@run failure(it) }

        // salt é gerado automaticamente, sendo diferente para cada password
        val passwordHash = passwordEncoder.encode(password)!!

        val userId = usersRepository.createUser(
            name = name,
            email = email,
            passwordHash = passwordHash,
            areaId = areaId
        )

        roles.forEach { role ->
            usersRepository.addUserRole(userId, role)
        }

        success(userId)
    }

    fun selectRole(
        token: String,
        role: String
    ): Either<UserError, Unit> = transactionManager.run {

        val claims = try {
            tokenService.parseAccessToken(token)
        } catch(e: Exception) {
            return@run failure(UserError.InvalidToken)
        }

        if(role !in claims.roles) {
            return@run failure(UserError.InvalidRoleSelection)
        }

        val updated = tokensRepository.updateActiveRole(
            token = token,
            activeRole = role
        )

        if(updated == 0) {
            return@run failure(UserError.InvalidToken)
        }

        success(Unit)
    }

    private fun validateLogin(user: User?, password: String): UserError? {
        return when {
            user == null -> UserError.InvalidCredentials
            !(user.isActive) -> UserError.UserNotActive
            !(passwordEncoder.matches(password, user.passwordHash)) ->
                UserError.InvalidCredentials

            else -> null
        }
    }

    private fun Transaction.validateUserCreation(email: String, password: String, roles: List<String>): UserError? {
        return when {
            usersRepository.isUserStoredByEmail(email) -> UserError.UserAlreadyExists
            password.length < 5 -> UserError.InsecurePassword
            roles.isEmpty() || roles.size > 5 || roles.any { it !in validRoles } -> UserError.InvalidRoles
            else -> null
        }
    }

    private fun Transaction.validateRefreshToken(refreshToken: String): UserError? {
        val refreshUserId = try {
            tokenService.parseRefreshToken(refreshToken)
        } catch (e: Exception) {
            return UserError.InvalidToken
        }

        val storedRefreshToken = refreshTokensRepository.getRefreshToken(refreshToken)
            ?: return UserError.RefreshTokenNotFound

        return when {
            storedRefreshToken.userId != refreshUserId -> UserError.InvalidToken
            storedRefreshToken.expiresAt.isBefore(java.time.Instant.now()) -> UserError.ExpiredRefreshToken
            tokensRepository.getAccessToken(storedRefreshToken.userId) == null -> UserError.InvalidToken
            else -> null
        }
    }

}