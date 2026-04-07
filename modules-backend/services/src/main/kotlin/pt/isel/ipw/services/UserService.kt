package pt.isel.ipw.services

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pt.isel.ipw.domain.User
import pt.isel.ipw.domain.output.LoginResponse
import pt.isel.ipw.repository.Transaction
import pt.isel.ipw.repository.TransactionManager
import pt.isel.ipw.services.auth.LoginResult
import pt.isel.ipw.services.auth.TokenService
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.Failure
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

        tokensRepository.deleteTokensByUserId(validUser.id) // atençao aqui -> isto significa 1 user = 1 sessao ativa
        val createdToken = tokenService.createLoginToken(validUser.id, roles)

        tokensRepository.createToken(
            createdToken.token,
            validUser.id,
            activeRole = null,
            expiresAt = createdToken.expiresAt
        )

        success(
            LoginResult(
                token = createdToken.token,
                userId = validUser.id,
                roles = roles,
                expiresAt = createdToken.expiresAt
            )
        )
    }

    fun getUserRoles(
        email: String
    ): Either<UserError, List<String>> = transactionManager.run {
        val user = usersRepository.getUserByEmail(email)
            ?: return@run failure(UserError.InvalidCredentials)

        Success(usersRepository.getUserRoles(user.id))
    }

    fun createUser(
        name: String,
        email: String,
        password: String,
        areaId: Int?,
        roles: List<String>
    ): Either<UserError, Int> = transactionManager.run {

        val error: UserError? = this.validateUserCreation(email, password, roles)
        error?.let { return@run failure(it) }

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
            tokenService.parseToken(token)
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

}