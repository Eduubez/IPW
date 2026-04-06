package pt.isel.ipw.http.controllers

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
import pt.isel.ipw.services.UserService

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/login")
    fun login(
        @RequestBody input: LoginRequest,
        response: HttpServletResponse
    ): LoginResponse {
        val result = userService.login(
            email = input.email,
            password = input.password
        )

        val cookie = Cookie("auth_token", result.token).apply {
            isHttpOnly = true
            secure = false
            maxAge = 120 * 60  // por enquanto hardcoded
        }

        response.addCookie(cookie)

        return LoginResponse(
            token = result.token,
            userId = result.userId,
            roles = result.roles,
            expiresAt = result.expiresAt.toString()
        )
    }

    @PostMapping("/auth/select-role")
    fun selectRole(
        @RequestBody body: SelectRoleRequest,
        request: HttpServletRequest
    ) {
        val token = request.cookies
            ?.firstOrNull { it.name == "auth_token" }
            ?.value
            ?: throw IllegalArgumentException("Missing auth token")

        userService.selectRole(
            token = token,
            role = body.role
        )
    }

    @GetMapping("/roles")
    fun roles(@RequestParam email: String): List<String> {
        return userService.getUserRoles(email)
    }

    @PostMapping
    fun createUser(@RequestBody body: CreateUserRequest): CreateUserResponse {
        val userId = userService.createUser(
            name = body.name,
            email = body.email,
            password = body.password,
            areaId = body.areaId,
            roles = body.roles
        )

        return CreateUserResponse(
            id = userId,
            name = body.name,
            email = body.email,
            areaId = body.areaId,
            roles = body.roles
        )
    }
}