package pt.isel.ipw.http.errors

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.Failure
import pt.isel.ipw.services.errors.Success

fun <E, T> handler(
    result: Either<E, T>,
    successStatus: HttpStatus,
    mapFunction: (E) -> Pair<Int, Problem>,
): ResponseEntity<*> =
    when (result) {
        is Success ->
            ResponseEntity
                .status(successStatus)
                .body(result.value)

        is Failure -> {
            val (status, problem) = mapFunction(result.value)
            Problem.response(status, problem)
        }
    }