package pt.isel.ipw.http.controllers

import jakarta.annotation.security.RolesAllowed
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.ipw.domain.DTO.input.area.UpdateAreaBossRequest
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.services.errors.Failure
import pt.isel.ipw.services.errors.Success
import pt.isel.ipw.services.interfaces.AreaService

@RestController
@RequestMapping("/api/area")
class AreaController(private val areaService: AreaService) {

    @GetMapping("/")
    @RolesAllowed(Roles.TRIATOR)
    fun getAllAreas(): ResponseEntity<*> {
        val result = areaService.getAllAreas()
        return when (result) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> ResponseEntity.badRequest().body(result)
        }
    }
    @GetMapping("/{id}")
    fun getAreaById(@PathVariable id:Int): ResponseEntity<*> {
        val result = areaService.getAreaById(id)

        return when (result) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> ResponseEntity.badRequest().body(result)
        }
    }
    @PostMapping("/{id}/boss")
    @RolesAllowed(Roles.ADMIN)
    fun updateBoss(@PathVariable id:Int, @RequestBody body: UpdateAreaBossRequest): ResponseEntity<*> {
        val result = areaService.updateAreaBoss(id, body.bossId)

        return when (result) {
            is Success -> ResponseEntity.ok(result.value)
            is Failure -> ResponseEntity.badRequest().body(result)
        }
    }

    @PostMapping("/new-area")
    @RolesAllowed(Roles.ADMIN)
    fun createArea(): ResponseEntity<*> {
        TODO()
    }
}
