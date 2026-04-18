package pt.isel.ipw.http.controllers

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import pt.isel.ipw.http.Cookies
import pt.isel.ipw.http.errors.Problem
import pt.isel.ipw.http.errors.handler
import pt.isel.ipw.services.auth.TokenService
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.Failure
import pt.isel.ipw.services.errors.HistoryError
import pt.isel.ipw.services.errors.Success
import pt.isel.ipw.services.errors.mapSuccess
import pt.isel.ipw.services.interfaces.IHistoryService


@RestController
@RequestMapping("/history")
class HistoryController(
    private val historyService: IHistoryService,
    private val tokenService: TokenService
) {
    @GetMapping("/{userId}")
    fun getUserHistory(
        @PathVariable userId: Int,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        val token = request.cookies
            ?.firstOrNull { it.name == Cookies.AUTH_COOKIE }
            ?.value
            ?: return Problem.response(HttpStatus.UNAUTHORIZED.value(), Problem.invalidToken)
        val claims = try {
            tokenService.parseAccessToken(token)
        } catch (e: Exception) {
            return Problem.response(HttpStatus.UNAUTHORIZED.value(), Problem.invalidToken)
        }
        val result = historyService.getUserHistory(userId,claims.roles.last())

        return when (result) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> {
                when (result.value) {
                    is HistoryError.InvalidUserId -> ResponseEntity.badRequest().body(result.value)
                    else -> ResponseEntity.badRequest().body(Problem.internalServerError)
                }
            }
        }
    }
    @GetMapping("/area/{areaId}")
    fun getAreaHistory(
        @PathVariable areaId: Int,
    ): ResponseEntity<*> {
        val result = historyService.getAreaHistory(areaId)
        return when(result) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> {
                when (result.value) {
                    is HistoryError.InvalidAreaId -> ResponseEntity.badRequest().body(result.value)
                    else -> ResponseEntity.badRequest().body(Problem.internalServerError)
                }
            }
        }
    }
}