package pt.isel.ipw.repository

import pt.isel.ipw.domain.User

interface UsersRepository {

    fun getUserByEmail(email: String): User?

    fun getUserRoles(userId: Int): List<String>

    fun isUserStoredByEmail(email: String): Boolean

    fun createUser(
        name: String,
        email: String,
        passwordHash: String,
        areaId: Int?
    ): Int

    fun addUserRole(userId: Int, roleName: String)
}