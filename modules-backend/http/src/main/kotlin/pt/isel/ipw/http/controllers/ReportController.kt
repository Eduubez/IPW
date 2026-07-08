package pt.isel.ipw.http.controllers

import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import pt.isel.ipw.domain.DTO.input.CreateReportRequest
import pt.isel.ipw.domain.DTO.input.UpdateReportRequest
import pt.isel.ipw.domain.DTO.output.CreateReportResponse
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.http.auth.AuthenticatedUser
import pt.isel.ipw.http.errors.Problem
import pt.isel.ipw.http.errors.handler
import pt.isel.ipw.http.errors.toHttp
import pt.isel.ipw.services.errors.mapSuccess
import pt.isel.ipw.services.interfaces.ReportService

@RestController
@RequestMapping("/api/process/{processId}/report")
class ReportController(
    private val reportService: ReportService
) {

    @RolesAllowed(Roles.INVESTIGATOR)
    @PostMapping
    fun createReport(
        @PathVariable processId: Int,
        @RequestBody report: CreateReportRequest
    ): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = reportService.createReport(processId, report.content, userId).mapSuccess {
            CreateReportResponse(it)
        }

        return handler(result, HttpStatus.CREATED) { error -> error.toHttp() }
    }
    @RolesAllowed(Roles.INVESTIGATOR,Roles.SUPERVISOR,Roles.MANAGER)
    @GetMapping
    fun getByProcessId(
        @PathVariable processId: Int,
    ): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidRoles)
        val result = reportService.getByProcessId(processId, userId, role)

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }
    @RolesAllowed(Roles.INVESTIGATOR)
    @PatchMapping
    fun updateReport(
        @PathVariable processId: Int,
        @RequestBody report: UpdateReportRequest
    ): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidRoles)

        val result = reportService.updateReport(processId, report.content, userId, role)

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @PostMapping("/approve")
    fun approveReport(
        @PathVariable processId: Int
    ): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)
        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidRoles)

        val result = reportService.approveReport(processId, userId, role)

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @PostMapping("/reject")
    fun rejectReport(
        @PathVariable processId: Int
    ): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)
        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidRoles)
        val result = reportService.rejectReport(processId, userId, role)

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }
}
