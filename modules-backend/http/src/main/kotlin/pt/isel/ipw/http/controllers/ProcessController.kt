package pt.isel.ipw.http.controllers

import com.sun.org.slf4j.internal.LoggerFactory
import jakarta.annotation.security.RolesAllowed
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import pt.isel.ipw.domain.DTO.input.CreateProcessRequest
import pt.isel.ipw.domain.DTO.input.UpdatePriorityRequest
import pt.isel.ipw.domain.DTO.input.UpdateProcessRequest
import pt.isel.ipw.domain.output.CreateProcessResponse
import pt.isel.ipw.domain.process.Priority
import pt.isel.ipw.domain.process.toResponse
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.http.ApiRoutes
import pt.isel.ipw.http.errors.handler
import pt.isel.ipw.http.errors.toHttp
import pt.isel.ipw.services.errors.mapSuccess
import pt.isel.ipw.services.interfaces.ProcessService

@RestController
@RequestMapping(ApiRoutes.Process.BASE)
class ProcessController(
    private val processService: ProcessService,
) {


    //@RolesAllowed(Roles.TRIATOR)
    @PostMapping
    fun createProcess(@RequestBody process: CreateProcessRequest, request: HttpServletRequest): ResponseEntity<*> {
        println("Create Process")

        val auth = SecurityContextHolder.getContext().authentication

        val userId = auth?.principal as Int

        println("Auth user id: $userId")

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

    //@RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)
    @GetMapping(ApiRoutes.Process.BY_ID)
    fun getProcessById(@PathVariable id: Int): ResponseEntity<*> {
        val auth = SecurityContextHolder.getContext().authentication
        val userId = auth?.principal as Int
        val role = auth.authorities.first().authority?.removePrefix("ROLE_")


        val result = processService.getProcessById(id, userId, role!!)
            .mapSuccess {
                it.toResponse()
            }

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    //@RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)
    @GetMapping
    fun getAllProcesses(
        @RequestParam(required = false) offset: Int,
        @RequestParam(required = false) limit: Int,
    ): ResponseEntity<*> {
        println("BEFORE PARSE TOKEN TO USER ID")
        val auth = SecurityContextHolder.getContext().authentication
        println("GETTING USER ID")

        val userId = auth?.principal as Int
        val role = auth.authorities.first().authority?.removePrefix("ROLE_")

        val result = processService.getAllProcesses(offset, limit, userId, role!!)
            .mapSuccess {
                it.map { it.toResponse() }
            }

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }


    @RolesAllowed(Roles.SUPERVISOR, Roles.MANAGER)
    @PatchMapping(ApiRoutes.Process.END_DATE_FULL)
    fun updateProcessEndDate(
        @PathVariable id: Int,
        @RequestBody endDate: String
    ): ResponseEntity<*> {
        val auth = SecurityContextHolder.getContext().authentication
        val userId = auth?.principal as Int
        val role = auth.authorities.first().authority?.removePrefix("ROLE_")


        val result = processService.changeEndDate(id, endDate, userId, role!!)

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @RolesAllowed(Roles.TRIATOR)
    @PatchMapping(ApiRoutes.Process.INVESTIGATOR_FULL)
    fun assignInvestigator(
        @PathVariable id: Int,
        @RequestBody investigatorId: Int
    ): ResponseEntity<*> {
        val auth = SecurityContextHolder.getContext().authentication
        val triatorId = auth?.principal as Int

        val result = processService.assignInvestigator(id, triatorId, investigatorId)

        // talvez retornar mensagem de sucesso 204 - No Content
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @RolesAllowed(Roles.TRIATOR)
    @PatchMapping(ApiRoutes.Process.SUPERVISOR_FULL)
    fun assignSupervisor(
        @PathVariable id: Int,
        @RequestBody supervisorId: Int
    ): ResponseEntity<*> {
        val auth = SecurityContextHolder.getContext().authentication
        val triatorId = auth?.principal as Int

        val result = processService.assignSupervisor(id, triatorId, supervisorId)

        // talvez retornar mensagem de sucesso 204 - No Content
        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }


    @RolesAllowed(Roles.SUPERVISOR, Roles.MANAGER)
    @PatchMapping(ApiRoutes.Process.PRIORITY_FULL)
    fun changePriority(@PathVariable id: Int, @RequestBody priority: UpdatePriorityRequest): ResponseEntity<*> {
        val auth = SecurityContextHolder.getContext().authentication
        val userId = auth?.principal as Int

        val result = processService.changePriority(id, priority.priority, userId)

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }

    }

    @RolesAllowed(Roles.MANAGER)
    @PatchMapping(ApiRoutes.Process.CANCEL_FULL)
    fun cancelProcess(@PathVariable id: Int): ResponseEntity<*> {
        val auth = SecurityContextHolder.getContext().authentication
        val userId = auth?.principal as Int

        val result = processService.cancelProcess(id, userId)

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }

    }

}