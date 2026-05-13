package pt.isel.ipw.services.results

import pt.isel.ipw.domain.DTO.output.user.AssignableUser
import pt.isel.ipw.domain.user.UserWithRoles
import pt.isel.ipw.services.auth.LoginResult
import pt.isel.ipw.services.auth.RefreshAccessToken
import pt.isel.ipw.services.auth.SelectRoleResult
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.UserError

typealias CreateUserResult = Either<UserError, Int>
typealias LoginResultResponse = Either<UserError, LoginResult>
typealias LogoutResult = Either<UserError, Unit>
typealias GetUserRolesResult = Either<UserError, List<String>>
typealias GetAllUsersResult = Either<UserError, List<UserWithRoles>>
typealias GetUserProfileInfoResult = Either<UserError, UserWithRoles>
typealias ChangeUserRolesResult = Either<UserError, Unit>
typealias ChangeUserPasswordResult = Either<UserError, Unit>
typealias RefreshAccessTokenResult = Either<UserError, RefreshAccessToken>
typealias SelectRoleServiceResult = Either<UserError, SelectRoleResult>
typealias GetAssignableUsersResult = Either<UserError, List<AssignableUser>>
