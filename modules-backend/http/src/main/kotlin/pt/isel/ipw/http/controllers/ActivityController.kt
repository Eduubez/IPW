package pt.isel.ipw.http.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.ipw.domain.DTO.output.toResponse
import pt.isel.ipw.http.errors.handler
import pt.isel.ipw.http.errors.toHttp
import pt.isel.ipw.services.errors.mapSuccess
import pt.isel.ipw.services.interfaces.ActivityService


@RestController
@RequestMapping("/activity")
class ActivityController(
    private val activityService: ActivityService
) {

    @GetMapping("/process/{id}")
    fun getActivitiesByProcess(
        @PathVariable id: Int,
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<*> {
        val result = activityService.getActivitiesByProcess(id, offset, limit)
            .mapSuccess { list -> list.toResponse() }
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @GetMapping("/users/{id}")
    fun getActivitiesByUser(
        @PathVariable id: Int,
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<*> {
        val result = activityService.getActivitiesByUser(id, offset, limit)
            .mapSuccess { list -> list.toResponse() }
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

}