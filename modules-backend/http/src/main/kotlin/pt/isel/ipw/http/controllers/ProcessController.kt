package pt.isel.ipw.http.controllers

import jakarta.annotation.security.RolesAllowed
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import pt.isel.ipw.domain.DTO.input.CreateProcessRequest
import pt.isel.ipw.domain.DTO.input.UpdatePriorityRequest
import pt.isel.ipw.domain.DTO.output.CreateProcessResponse
import pt.isel.ipw.domain.DTO.output.ListResponse
import pt.isel.ipw.domain.process.toResponse
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.http.auth.AuthenticatedUser
import pt.isel.ipw.http.errors.Problem
import pt.isel.ipw.http.errors.handler
import pt.isel.ipw.http.errors.toHttp
import pt.isel.ipw.services.errors.mapSuccess
import pt.isel.ipw.services.interfaces.ProcessService

@RestController
@RequestMapping("/api/process")
class ProcessController(
    private val processService: ProcessService,
) {


    @RolesAllowed(Roles.TRIATOR)
    @PostMapping
    fun createProcess(@RequestBody process: CreateProcessRequest): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = processService.createProcess(
            userId = userId,
            name = process.name,
            street = process.street,
            county = process.county,
            district = process.district,
            latitude = process.latitude,
            longitude = process.longitude,
            area = process.area,
            priority = process.priority,
            expiresAt = process.expiresAt,
            investigatorId = process.investigatorId,
            supervisorId = process.supervisorId,
            insuranceId = process.insuranceId,
            typificationId = process.typificationId,
            canBeFraud = process.canBeFraud,
            note = process.note,
        ).mapSuccess {
            CreateProcessResponse(
                id = it,
            )
        }

        return handler(result, HttpStatus.CREATED) { error -> error.toHttp() }
    }

    @RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)
    @GetMapping("/{id}")
    fun getProcessById(@PathVariable id: Int): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)
        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = processService.getProcessById(id, userId, role)
            .mapSuccess {
                it.toResponse()
            }

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @RolesAllowed(Roles.INVESTIGATOR)
    @PostMapping("/{id}/submit")
    fun submitProcess(
        @PathVariable id: Int,

    ): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)
        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = processService.submitProcess(userId, role, id).mapSuccess {
            it
        }

        return handler(result, HttpStatus.NO_CONTENT) { error -> error.toHttp() }
    }

    @RolesAllowed(Roles.TRIATOR, Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)
    @GetMapping
    fun getAllProcesses(
        @RequestParam(required = false) offset: Int?,
        @RequestParam(required = false) limit: Int?,
        @RequestParam(required = false) areaId: Int?,
    ): ResponseEntity<*> {

        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)
        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = processService.getAllProcesses(offset, limit, areaId, userId, role)
            .mapSuccess { processes ->
                ListResponse(
                    results = processes.map { it.toResponse() }
                )
            }

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }


    @RolesAllowed(Roles.SUPERVISOR, Roles.MANAGER)
    @PatchMapping("/{id}/end-date")
    fun updateProcessEndDate(
        @PathVariable id: Int,
        @RequestBody endDate: String
    ): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)
        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidToken)


        val result = processService.changeEndDate(id, endDate, userId, role)

        return handler(result, HttpStatus.NO_CONTENT) { error -> error.toHttp() }
    }

    @RolesAllowed(Roles.TRIATOR)
    @PatchMapping("/{id}/investigator")
    fun assignInvestigator(
        @PathVariable id: Int,
        @RequestBody investigatorId: Int
    ): ResponseEntity<*> {
        val triatorId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = processService.assignInvestigator(id, triatorId, investigatorId)

        return handler(result, HttpStatus.NO_CONTENT) { error -> error.toHttp() }
    }

    @RolesAllowed(Roles.TRIATOR)
    @PatchMapping("/{id}/supervisor")
    fun assignSupervisor(
        @PathVariable id: Int,
        @RequestBody supervisorId: Int
    ): ResponseEntity<*> {
        val triatorId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = processService.assignSupervisor(id, triatorId, supervisorId)

        return handler(result, HttpStatus.NO_CONTENT) { error -> error.toHttp() }
    }


    @RolesAllowed(Roles.SUPERVISOR, Roles.MANAGER)
    @PatchMapping("/{id}/priority")
    fun changePriority(@PathVariable id: Int, @RequestBody priority: UpdatePriorityRequest): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)
        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidToken)


        val result = processService.changePriority(id, priority.priority, userId, role)

        return handler(result, HttpStatus.NO_CONTENT) { error -> error.toHttp() }
    }

    @RolesAllowed(Roles.MANAGER)
    @PostMapping("/{id}/cancel")
    fun cancelProcess(@PathVariable id: Int): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = processService.cancelProcess(id, userId)

        return handler(result, HttpStatus.NO_CONTENT) { error -> error.toHttp() }

    }

}
