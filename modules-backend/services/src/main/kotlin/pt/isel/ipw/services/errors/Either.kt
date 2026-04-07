package pt.isel.ipw.services.errors

sealed class Either<out L, out R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>()

    data class Right<out R>(val value: R) : Either<Nothing, R>()

    inline fun <T> fold(
        onFailure: (L) -> T,
        onSuccess: (R) -> T
    ): T = when (this) {
        is Left -> onFailure(value)
        is Right -> onSuccess(value)
    }
}

// Functions for when using Either to represent success or failure
fun <R> success(value: R) = Either.Right(value)

fun <L> failure(error: L) = Either.Left(error)

fun <L, R, T> Either<L, R>.mapSuccess(transform: (R) -> T) : Either<L, T> {
    return when (this) {
        is Success -> Success(transform(this.value))
        is Failure -> this
    }
}

typealias Success<S> = Either.Right<S>
typealias Failure<F> = Either.Left<F>