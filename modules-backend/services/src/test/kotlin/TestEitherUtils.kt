import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.Failure
import pt.isel.ipw.services.errors.Success


object TestEitherUtils {

    fun <E, T> assertSuccess(result: Either<E, T>): T =
        when (result) {
            is Success -> result.value
            is Failure -> error("Expected success but got ${result.value}")
        }

    fun <E, T> assertFailure(result: Either<E, T>): E =
        when (result) {
            is Failure -> result.value
            is Success -> error("Expected failure but got ${result.value}")
        }
}
