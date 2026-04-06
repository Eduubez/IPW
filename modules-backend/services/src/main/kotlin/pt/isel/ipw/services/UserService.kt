package pt.isel.ipw.services

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pt.isel.ipw.domain.output.LoginResponse
import pt.isel.ipw.repository.TransactionManager
import pt.isel.ipw.services.auth.LoginResult
import pt.isel.ipw.services.auth.TokenService

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
    ): LoginResult = transactionManager.run {
        val user = usersRepository.getUserByEmail(email)
            ?: throw IllegalArgumentException("Invalid credentials")

        if (!user.isActive) {
            throw IllegalArgumentException("User is inactive")
        }

        if (!passwordEncoder.matches(password, user.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        val roles = usersRepository.getUserRoles(user.id)
        val createdToken = tokenService.createLoginToken(user.id, roles)

        tokensRepository.createToken(
            createdToken.token,
            user.id,
            activeRole = null,
            expiresAt = createdToken.expiresAt
        )

        LoginResult(
            token = createdToken.token,
            userId = user.id,
            roles = roles,
            expiresAt = createdToken.expiresAt
        )
    }

    fun getUserRoles(
        email: String
    ): List<String> = transactionManager.run {
        val user = usersRepository.getUserByEmail(email)
            ?: return@run emptyList()

        usersRepository.getUserRoles(user.id)
    }

    fun createUser(
        name: String,
        email: String,
        password: String,
        areaId: Int?,
        roles: List<String>
    ): Int = transactionManager.run {
        if(usersRepository.isUserStoredByEmail(email)) {
            throw IllegalArgumentException("User with email $email already exists")
        }

        if (password.length < 5) {
            throw IllegalArgumentException("Password must have at least 5 characters")
        }


        if (roles.isEmpty() || roles.size > 5 || roles.any { it !in validRoles } ) {
            throw IllegalArgumentException("User must have at least one role")
        }

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

        userId
    }


    fun selectRole(
        token: String,
        role: String
    ) = transactionManager.run {

        val claims = tokenService.parseToken(token)

        if(role !in claims.roles) {
            throw IllegalArgumentException("Role $role is not valid for this user")
        }

        val updated = tokensRepository.updateActiveRole(
            token = token,
            activeRole = role
        )

        if(updated == 0) {
            throw IllegalArgumentException("Invalid token")
        }
    }
}