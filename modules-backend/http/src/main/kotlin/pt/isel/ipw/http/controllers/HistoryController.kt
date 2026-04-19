package pt.isel.ipw.http.controllers

import jakarta.annotation.security.RolesAllowed
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import pt.isel.ipw.domain.roles.Roles
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
) {
    @RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.TRIATOR, Roles.MANAGER)
    @GetMapping("/{userId}")
    fun getUserHistory(
        @PathVariable userId: Int,
    ): ResponseEntity<*> {
        val auth = SecurityContextHolder.getContext().authentication
        val userRole = auth?.authorities?.last()?.authority?.removePrefix("ROLE_")?.lowercase() ?: "" // takin the last at this point , while we dont have active role implemented

        val result = historyService.getUserHistory(userId, userRole)

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
    @RolesAllowed(Roles.SUPERVISOR,Roles.MANAGER)
    @GetMapping("/area/{areaId}")
    fun getAreaHistory(
        @PathVariable areaId: Int,
    ): ResponseEntity<*> {
        val auth = SecurityContextHolder.getContext().authentication
        val subject = auth?.principal as? Int

        val result = historyService.getAreaHistory(subject!!,areaId)
        return when(result) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> {
                when (result.value) {
                    is HistoryError.InvalidAreaId -> ResponseEntity.badRequest().body(result.value)
                    is HistoryError.Forbidden -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(result.value)
                    is HistoryError.AreaNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.value)
                    else -> ResponseEntity.badRequest().body(Problem.internalServerError)
                }
            }
        }
    }
}