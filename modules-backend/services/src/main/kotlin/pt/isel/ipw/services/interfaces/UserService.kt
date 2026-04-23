package pt.isel.ipw.services.interfaces

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

    fun refreshAccessToken(refreshToken: String): Either<UserError, RefreshAccessToken>

    fun getUserRoles(email: String): Either<UserError, List<String>>

    fun selectRole(loginToken: String, role: String): Either<UserError, SelectRoleResult>
}