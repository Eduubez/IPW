package pt.isel.ipw.http.controllers

import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.ipw.domain.DTO.output.ListResponse
import pt.isel.ipw.domain.DTO.output.toResponse
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.http.errors.handler
import pt.isel.ipw.http.errors.toHttp
import pt.isel.ipw.services.errors.mapSuccess
import pt.isel.ipw.services.interfaces.ActivityService


@RestController
@RequestMapping("/api/activity")
class ActivityController(
    private val activityService: ActivityService
) {

    @GetMapping("/process/{id}")
    @RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)
    fun getActivitiesByProcess(
        @PathVariable id: Int,
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<*> {
        val result = activityService.getActivitiesByProcess(id, offset, limit)
            .mapSuccess { list ->
                ListResponse(
                    results = list.toResponse()
                )
            }
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @GetMapping("/users/{id}")
    fun getActivitiesByUser(
        @PathVariable id: Int,
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<*> {
        val result = activityService.getActivitiesByUser(id, offset, limit)
            .mapSuccess { list ->
                ListResponse(
                    results = list.toResponse()
                )
            }
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

}
