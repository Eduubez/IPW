package pt.isel.ipw.http.controllers

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
import pt.isel.ipw.domain.output.TokenResponse
import pt.isel.ipw.domain.output.UserRolesResponse
import pt.isel.ipw.http.errors.Problem
import pt.isel.ipw.http.errors.handler
import pt.isel.ipw.http.errors.toHttp
import pt.isel.ipw.services.UserService
import pt.isel.ipw.services.errors.mapSuccess

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/login")
    fun login(
        @RequestBody input: LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val result = userService.login(input.email, input.password)
            .mapSuccess {
                val cookie = Cookie("auth_token", it.token).apply {
                    isHttpOnly = true
                    secure = false // false agora pois em localhost o cliente nao pode enviar o cookie de volta
                    path = "/" // para cookie ficar disponivel em todos os endpoints
                    maxAge = 120 * 60  // por enquanto hardcoded
                }

                response.addCookie(cookie)

                LoginResponse(
                    token = TokenResponse(
                        value = it.token,
                        expiresAt = it.expiresAt.toString()
                    ),
                    userId = it.userId,
                    roles = it.roles,
                )
            }

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @PostMapping("/auth/select-role")
    fun selectRole(
        @RequestBody body: SelectRoleRequest,
        request: HttpServletRequest
    ) : ResponseEntity<*> {
        val token = request.cookies
            ?.firstOrNull { it.name == "auth_token" } // hardcoded
            ?.value
            ?: return Problem.response(HttpStatus.UNAUTHORIZED.value(), Problem.invalidToken)

        val result = userService.selectRole(token, body.role)
        return handler(result, HttpStatus.NO_CONTENT) { error -> error.toHttp() }
    }

    @GetMapping("/roles")
    fun roles(@RequestParam email: String): ResponseEntity<*> {
        val result = userService.getUserRoles(email)
            .mapSuccess { roles ->
                UserRolesResponse(roles)
            }
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

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
}