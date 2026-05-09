package pt.isel.ipw.repository

import pt.isel.ipw.domain.user.User
import pt.isel.ipw.domain.user.UserWithRoles

interface UsersRepository {

    fun createUser(
        name: String,
        email: String,
        passwordHash: String,
        areaId: Int?
    ): Int

    fun getUserByEmail(email: String): User?

    fun getUserRoles(userId: Int): List<String>

    fun isUserStoredByEmail(email: String): Boolean

    fun isUserStoredById(userId: Int): Boolean

    fun addUserRoles(userId: Int, roles: List<String>)

    fun getUserById(userId: Int): User?

    fun getUserWithRolesById(userId: Int): UserWithRoles?

    fun getAllUsers(offset: Int, limit: Int): List<UserWithRoles>

    fun replaceUserRoles(userId: Int, roles: List<String>)

    fun updateUserPassword(userId: Int, newPasswordHash: String)

    fun updateUserArea(userId: Int, areaId: Int?)
}
