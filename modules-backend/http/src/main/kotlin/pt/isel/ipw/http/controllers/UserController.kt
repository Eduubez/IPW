package pt.isel.ipw.http.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.ipw.domain.input.CreateUserRequest
import pt.isel.ipw.domain.input.LoginRequest
import pt.isel.ipw.domain.input.SelectRoleRequest
import pt.isel.ipw.domain.output.CreateUserResponse
import pt.isel.ipw.domain.output.LoginResponse
import pt.isel.ipw.domain.output.RefreshTokenResponse
import pt.isel.ipw.domain.output.SelectRoleResponse
import pt.isel.ipw.domain.output.TokenResponse
import pt.isel.ipw.domain.output.UserRolesResponse
import pt.isel.ipw.http.ApiRoutes
import pt.isel.ipw.http.auth.AuthenticatedLogin
import pt.isel.ipw.http.auth.AuthenticatedRefresh
import pt.isel.ipw.http.auth.LoginTokenPrincipal
import pt.isel.ipw.http.auth.RefreshTokenPrincipal
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
    fun createUser(@RequestBody body: CreateUserRequest): ResponseEntity<*> {
        val result = userService.createUser(body.name, body.email, body.password, body.areaId, body.roles)
            .mapSuccess { userId ->
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
    ) : ResponseEntity<*> {
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
                    role = it.role
                )
            }

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
}