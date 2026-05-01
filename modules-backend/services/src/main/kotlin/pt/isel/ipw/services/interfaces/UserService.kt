package pt.isel.ipw.services.interfaces

import pt.isel.ipw.domain.user.UserWithRoles
import pt.isel.ipw.services.auth.LoginResult
import pt.isel.ipw.services.auth.RefreshAccessToken
import pt.isel.ipw.services.auth.SelectRoleResult
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.UserError

interface UserService {
    fun createUser(
        name: String,
        email: String,
        password: String,
        areaId: Int?,
        roles: List<String>
    ): Either<UserError, Int>

    fun login(email: String, password: String): Either<UserError, LoginResult>

    fun getUserRoles(email: String): Either<UserError, List<String>>

    fun getAllUsers(offset: Int, limit: Int): Either<UserError, List<UserWithRoles>>

    fun changeUserRoles(
        userId: Int,
        roles: List<String>,
        areaId: Int?,
    ): Either<UserError, Unit>

    fun changeUserPassword(userId: Int, newPassword: String): Either<UserError, Unit>

    fun refreshAccessToken(
        refreshToken: String,
        userId: Int,
        role: String
    ): Either<UserError, RefreshAccessToken>

    fun selectRole(
        loginToken: String,
        userId: Int,
        selectedRole: String
    ): Either<UserError, SelectRoleResult>
}