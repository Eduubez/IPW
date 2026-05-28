package pt.isel.ipw.services.errors

sealed class AreaError(
    val status: Int,
) {
    data object AreaNotFound : AreaError( 404)
    data object InvalidAreaId : AreaError(400)
    data object InvalidUserId : AreaError( 400)
    data object UserNotFound : AreaError(404)
}