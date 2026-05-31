package pt.isel.ipw.http.errors

import org.springframework.http.ResponseEntity
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import pt.isel.ipw.services.auth.ExpiredAccessTokenException
import pt.isel.ipw.services.auth.ExpiredLoginTokenException
import pt.isel.ipw.services.auth.ExpiredRefreshTokenException
import pt.isel.ipw.services.auth.InvalidTokenException
import pt.isel.ipw.services.errors.UserError

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class TokenExceptionHandler {

    @ExceptionHandler(ExpiredLoginTokenException::class)
    fun handleExpiredLoginToken(): ResponseEntity<*> {
        val (status, problem) = UserError.ExpiredLoginToken.toHttp()
        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(ExpiredAccessTokenException::class)
    fun handleExpiredAccessToken(): ResponseEntity<*> {
        val (status, problem) = UserError.ExpiredAccessToken.toHttp()
        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(ExpiredRefreshTokenException::class)
    fun handleExpiredRefreshToken(): ResponseEntity<*> {
        val (status, problem) = UserError.ExpiredRefreshToken.toHttp()
        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidToken(): ResponseEntity<*> {
        val (status, problem) = UserError.InvalidToken.toHttp()
        return ResponseEntity.status(status).body(problem)
    }
}
