package pt.isel.ipw.http.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ipw.http.errors.handler
import pt.isel.ipw.http.errors.toHttp
import pt.isel.ipw.services.interfaces.UserService

@RestController
@RequestMapping("/api/public")
class PublicController(private val userService: UserService) {

    @GetMapping("/contacts")
    fun adminContact(): ResponseEntity<*> {
        val result = userService.getAdminInformation()
        return handler (result,HttpStatus.OK) { error -> error.toHttp() }
    }

}