package pt.isel.ipw.http.controllers

import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ipw.domain.DTO.input.prove.CreateProveRequest
import pt.isel.ipw.domain.DTO.input.prove.CreateProveUploadUrlRequest
import pt.isel.ipw.domain.DTO.output.ListResponse
import pt.isel.ipw.domain.DTO.output.prove.CreateProveResponse
import pt.isel.ipw.domain.DTO.output.prove.ProveResponse
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.http.ApiRoutes
import pt.isel.ipw.http.auth.AuthenticatedUser
import pt.isel.ipw.http.errors.Problem
import pt.isel.ipw.http.errors.handler
import pt.isel.ipw.http.errors.toHttp
import pt.isel.ipw.services.errors.mapSuccess
import pt.isel.ipw.services.interfaces.ProveService

@RestController
@RequestMapping(ApiRoutes.Process.BASE)
class ProveController(
    private val proveService: ProveService,
) {

    @RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)
    @PostMapping(ApiRoutes.Process.PROVE_UPLOAD_URL)
    fun createUploadUrl(
        @PathVariable id: Int,
        @RequestBody body: CreateProveUploadUrlRequest
    ): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = proveService.createUploadUrl(
            processId = id,
            userId = userId,
            role = role,
            fileName = body.fileName,
            contentType = body.contentType,
            fileSize = body.fileSize,
        )

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)
    @PostMapping(ApiRoutes.Process.PROVES)
    fun createProve(
        @PathVariable id: Int,
        @RequestBody body: CreateProveRequest
    ): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = proveService.createProve(
            processId = id,
            userId = userId,
            role = role,
            fileName = body.fileName,
            contentType = body.contentType,
            fileSize = body.fileSize,
            storageKey = body.storageKey,
        ).mapSuccess { proveId ->
            CreateProveResponse(id = proveId)
        }

        return handler(result, HttpStatus.CREATED) { error -> error.toHttp() }
    }

    @RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)
    @GetMapping(ApiRoutes.Process.PROVES)
    fun getProcessProves(
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = proveService.getProcessProves(
            processId = id,
            userId = userId,
            role = role,
        ).mapSuccess { proves ->
            ListResponse(
                results = proves.map { prove ->
                    ProveResponse(
                        id = prove.id,
                        processId = prove.processId,
                        fileName = prove.fileName,
                        contentType = prove.contentType,
                        fileSize = prove.fileSize,
                        createdBy = prove.createdBy,
                        createdAt = prove.createdAt.toString(),
                    )
                }
            )
        }

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)
    @GetMapping(ApiRoutes.Process.PROVE_ACCESS_URL)
    fun getProveAccessUrl(
        @PathVariable id: Int,
        @PathVariable proveId: Int
    ): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = proveService.getProveAccessUrl(
            processId = id,
            proveId = proveId,
            userId = userId,
            role = role,
        )

        return handler(result, HttpStatus.OK) { error -> error.toHttp() }
    }

    @RolesAllowed(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.MANAGER)
    @DeleteMapping(ApiRoutes.Process.PROVE_BY_ID)
    fun deleteProve(
        @PathVariable id: Int,
        @PathVariable proveId: Int
    ): ResponseEntity<*> {
        val userId = AuthenticatedUser.id()
            ?: return Problem.response(401, Problem.invalidToken)

        val role = AuthenticatedUser.role()
            ?: return Problem.response(401, Problem.invalidToken)

        val result = proveService.deleteProve(
            processId = id,
            proveId = proveId,
            userId = userId,
            role = role,
        )

        return handler(result, HttpStatus.NO_CONTENT) { error -> error.toHttp() }
    }
}
