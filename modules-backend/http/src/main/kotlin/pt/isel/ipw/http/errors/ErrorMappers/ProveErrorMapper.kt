package pt.isel.ipw.http.errors

import pt.isel.ipw.services.errors.ProveError

private val proveErrorMap = mapOf(
    ProveError.ProcessNotFound to Problem.processNotFound,
    ProveError.ProveNotFound to Problem.proveNotFound,
    ProveError.UnauthorizedAccess to Problem.unauthorized,
    ProveError.InvalidFileName to Problem.invalidFileName,
    ProveError.InvalidContentType to Problem.invalidContentType,
    ProveError.InvalidFileSize to Problem.invalidFileSize,
    ProveError.InvalidStorageKey to Problem.invalidStorageKey,
    ProveError.StorageError to Problem.storageError
)

fun ProveError.toHttp(): Pair<Int, Problem> =
    this.status to (proveErrorMap[this] ?: Problem.internalServerError)
