package pt.isel.ipw.http.controllers

import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.ipw.domain.DTO.input.ChangeUserPasswordRequest
import pt.isel.ipw.domain.DTO.input.ChangeUserRolesRequest
import pt.isel.ipw.domain.DTO.input.CreateUserRequest
import pt.isel.ipw.domain.DTO.input.LoginRequest
import pt.isel.ipw.domain.DTO.input.SelectRoleRequest
import pt.isel.ipw.domain.DTO.output.AssignableUserResponse
import pt.isel.ipw.domain.DTO.output.CreateUserResponse
import pt.isel.ipw.domain.DTO.output.ListResponse
import pt.isel.ipw.domain.DTO.output.LoginResponse
import pt.isel.ipw.domain.DTO.output.RefreshTokenResponse
import pt.isel.ipw.domain.DTO.output.SelectRoleResponse
import pt.isel.ipw.domain.DTO.output.TokenResponse
import pt.isel.ipw.domain.DTO.output.user.UserRolesResponse
import pt.isel.ipw.domain.DTO.output.user.AdminUserResponse
import pt.isel.ipw.domain.DTO.output.user.UserProfileResponse
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.http.ApiRoutes
import pt.isel.ipw.http.auth.AuthenticatedLogin
import pt.isel.ipw.http.auth.AuthenticatedRefresh
import pt.isel.ipw.http.auth.LoginTokenPrincipal
import pt.isel.ipw.http.auth.RefreshTokenPrincipal
import pt.isel.ipw.http.errors.Problem
import pt.isel.ipw.http.errors.handler
import pt.isel.ipw.http.errors.toHttp
import pt.isel.ipw.services.errors.mapSuccess
import pt.isel.ipw.services.interfaces.UserService

@RestController
@RequestMapping(ApiRoutes.Users.BASE)
class UserController(
    private val userService: UserService
) {

    @PostMapping
    //@RolesAllowed(Roles.ADMIN)
    fun createUser(@RequestBody body: CreateUserRequest): ResponseEntity<*> {
        val result = userService.createUser(
            body.name,
            body.email,
            body.password,
            body.areaId,
            body.roles
        ).mapSuccess { userId ->
            CreateUserResponse(
                id = userId,
                name = body.name,
                email = body.email,
                areaId = body.areaId,
                roles = body.roles
            )
        }
        return handler(result, HttpStatus.CREATED) { error -> error.toHttp() }
    }


    @GetMapping("/me")
    @RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.TRIATOR, Roles.MANAGER, Roles.ADMIN)
    fun getMe(): ResponseEntity<*> {
        val userId = authenticatedUserId()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = userService.getUserProfileInfo(userId)
            .mapSuccess { user ->
                UserProfileResponse(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                    areaId = user.areaId,
                    area = user.area,
                    roles = user.roles
                )
            }

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @GetMapping
    @RolesAllowed(Roles.ADMIN)
    fun getAllUsers(
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<*> {
        val result = userService.getAllUsers(offset, limit)
            .mapSuccess { users ->
                ListResponse(
                    results = users.map {
                        AdminUserResponse(
                            id = it.id,
                            name = it.name,
                            email = it.email,
                            areaId = it.areaId,
                            area = it.area,
                            isActive = it.isActive,
                            roles = it.roles
                        )
                    }
                )
            }
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @PostMapping(ApiRoutes.Users.LOGIN)
    fun login(
        @RequestBody input: LoginRequest,
    ): ResponseEntity<*> {
        val result = userService.login(input.email, input.password)
            .mapSuccess {
                LoginResponse(
                    loginToken = TokenResponse(
                        value = it.loginToken,
                        expiresAt = it.expiresAt.toString()
                    ),
                    userId = it.userId,
                    roles = it.roles,
                )
            }
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @PostMapping(ApiRoutes.Users.LOGOUT)
    fun logout(): ResponseEntity<*> {
        val userId = authenticatedUserId()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = userService.logout(userId)
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @PostMapping(ApiRoutes.Users.REFRESH_TOKEN)
    fun refreshToken(
        @AuthenticatedRefresh refreshToken: RefreshTokenPrincipal
    ): ResponseEntity<*> {
        val result = userService.refreshAccessToken(
            refreshToken = refreshToken.token,
            userId = refreshToken.claims.userId,
            role = refreshToken.claims.role
        ).mapSuccess {
            RefreshTokenResponse(
                token = TokenResponse(
                    value = it.token,
                    expiresAt = it.expiresAt.toString()
                )
            )
        }
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @PostMapping(ApiRoutes.Users.SELECT_ROLE)
    fun selectRole(
        @RequestBody body: SelectRoleRequest,
        @AuthenticatedLogin loginToken: LoginTokenPrincipal
    ): ResponseEntity<*> {
        val result = userService.selectRole(
            loginToken = loginToken.token,
            userId = loginToken.claims.userId,
            selectedRole = body.role
        ).mapSuccess {
            SelectRoleResponse(
                accessToken = TokenResponse(
                    value = it.accessToken,
                    expiresAt = it.accessTokenExpiresAt.toString()
                ),
                refreshToken = TokenResponse(
                    value = it.refreshToken,
                    expiresAt = it.refreshTokenExpiresAt.toString()
                ),
                role = it.role,
                areaId = it.areaId,
                area = it.area
            )
        }
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @PutMapping("/{userId}/roles")
    @RolesAllowed(Roles.ADMIN)
    fun changeUserRoles(
        @PathVariable userId: Int,
        @RequestBody body: ChangeUserRolesRequest
    ): ResponseEntity<*> {
        val result = userService.changeUserRoles(userId, body.roles, body.areaId)
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @PutMapping("/{userId}/password")
    @RolesAllowed(Roles.ADMIN)
    fun changeUserPassword(
        @PathVariable userId: Int,
        @RequestBody body: ChangeUserPasswordRequest
    ): ResponseEntity<*> {
        val result = userService.changeUserPassword(userId, body.newPassword)
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @GetMapping(ApiRoutes.Users.ROLES)
    fun roles(@RequestParam email: String): ResponseEntity<*> {
        val result = userService.getUserRoles(email)
            .mapSuccess { roles ->
                UserRolesResponse(roles)
            }
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }


    @GetMapping(ApiRoutes.Users.INVESTIGATORS)
    @RolesAllowed(Roles.TRIATOR)
    fun getAllInvestigators(
        @RequestParam(required = false) areaId: Int?
    ): ResponseEntity<*> {
        val result = userService.getAllInvestigators(areaId)
            .mapSuccess { users ->
                ListResponse(
                    results = users.map {
                        AssignableUserResponse(
                            id = it.id,
                            name = it.name,
                            areaId = it.areaId,
                            area = it.area
                        )
                    }
                )
            }

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @GetMapping(ApiRoutes.Users.SUPERVISORS)
    @RolesAllowed(Roles.TRIATOR)
    fun getAllSupervisors(
        @RequestParam(required = false) areaId: Int?
    ): ResponseEntity<*> {
        val result = userService.getAllSupervisors(areaId)
            .mapSuccess { users ->
                ListResponse(
                    results = users.map {
                        AssignableUserResponse(
                            id = it.id,
                            name = it.name,
                            areaId = it.areaId,
                            area = it.area
                        )
                    }
                )
            }

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }


    private fun authenticatedUserId(): Int? =
        SecurityContextHolder
            .getContext()
            .authentication
            ?.principal as? Int
}
