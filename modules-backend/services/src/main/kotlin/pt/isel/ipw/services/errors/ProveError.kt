package pt.isel.ipw.services.errors

sealed class ProveError(
    val status: Int
) {
    data object ProcessNotFound : ProveError(404)
    data object ProveNotFound : ProveError(404)
    data object UnauthorizedAccess : ProveError (403)
    data object InvalidFileName : ProveError(400)
    data object InvalidContentType : ProveError(400)
    data object InvalidFileSize : ProveError(400)
    data object InvalidStorageKey : ProveError(400)
    data object StorageError : ProveError(500)
    data object ProcessFinished : ProveError(400)
}