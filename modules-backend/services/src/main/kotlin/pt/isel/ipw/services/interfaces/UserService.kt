package pt.isel.ipw.services.interfaces

import pt.isel.ipw.services.results.ChangeUserPasswordResult
import pt.isel.ipw.services.results.ChangeUserRolesResult
import pt.isel.ipw.services.results.CreateUserResult
import pt.isel.ipw.services.results.GetAllUsersResult
import pt.isel.ipw.services.results.GetAssignableUsersResult
import pt.isel.ipw.services.results.GetUserProfileInfoResult
import pt.isel.ipw.services.results.GetUserRolesResult
import pt.isel.ipw.services.results.LoginResultResponse
import pt.isel.ipw.services.results.LogoutResult
import pt.isel.ipw.services.results.RefreshAccessTokenResult
import pt.isel.ipw.services.results.SelectRoleServiceResult

interface UserService {
    fun createUser(
        name: String,
        email: String,
        password: String,
        areaId: Int?,
        roles: List<String>
    ): CreateUserResult

    fun login(email: String, password: String): LoginResultResponse

    fun logout(userId: Int): LogoutResult

    fun getUserRoles(email: String): GetUserRolesResult

    fun getAllUsers(offset: Int, limit: Int): GetAllUsersResult

    fun getUserProfileInfo(userId: Int): GetUserProfileInfoResult

    fun getAllInvestigators(areaId: Int?): GetAssignableUsersResult

    fun getAllSupervisors(areaId: Int?): GetAssignableUsersResult

    fun changeUserRoles(
        userId: Int,
        roles: List<String>,
        areaId: Int?,
    ): ChangeUserRolesResult

    fun changeUserPassword(userId: Int, newPassword: String): ChangeUserPasswordResult

    fun refreshAccessToken(
        refreshToken: String,
        userId: Int,
        role: String
    ): RefreshAccessTokenResult

    fun selectRole(
        loginToken: String,
        userId: Int,
        selectedRole: String
    ): SelectRoleServiceResult
}
