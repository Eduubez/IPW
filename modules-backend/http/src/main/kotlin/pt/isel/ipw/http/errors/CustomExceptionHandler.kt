package pt.isel.ipw.http.errors

import org.springframework.beans.TypeMismatchException
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.ErrorResponse
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.logging.Level
import java.util.logging.Logger

@RestControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception): ResponseEntity<*> {
        val status = ex.httpStatus()

        if (status >= 500) {
            log.log(Level.SEVERE, "Handling unexpected exception", ex)
            return Problem.response(status, Problem.internalServerError)
        }

        log.info("Handling ${ex.javaClass.simpleName}: ${ex.message}")

        return Problem.response(
            status,
            Problem(
                type = "problems/${ex.problemType()}",
                message = ex.safeMessage(),
                errorCode = ex.errorCode()
            )
        )
    }

    private fun Exception.httpStatus(): Int =
        when (this) {
            is ErrorResponse -> this.statusCode.value()
            is TypeMismatchException -> 400
            is AccessDeniedException -> 403
            else -> 500
        }

    private fun Exception.errorCode(): ErrorCode =
        when (this) {
            is AccessDeniedException -> ErrorCode.UNAUTHORIZED
            else -> ErrorCode.REQUEST_ERROR
        }

    private fun Exception.problemType(): String =
        javaClass.simpleName
            .removeSuffix("Exception")
            .replace(Regex("([a-z])([A-Z])"), "$1-$2")
            .lowercase()

    private fun Exception.safeMessage(): String =
        when (this) {
            is HttpRequestMethodNotSupportedException -> "HTTP method is not supported"
            is HttpMediaTypeNotSupportedException -> "HTTP media type is not supported"
            else -> this.message ?: "Invalid request"
        }

    companion object {
        private val log = Logger.getLogger(CustomExceptionHandler::class.java.name)
    }
}
