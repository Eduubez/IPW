package pt.isel.ipw.services.errors

sealed class AreaError(
    val code: String,
    val status: Int,
    val message: String
) {
    data object AreaNotFound : AreaError("AreaNotFound", 404, "The specified area does not exist")
    data object InvalidAreaId : AreaError("InvalidAreaId", 400, "The provided area ID is invalid")
    data object InvalidUserId : AreaError("InvalidUserId", 400, "The provided user ID is invalid")
        data object UserNotFound : AreaError("UserNotFound", 404, "The specified user does not exist")
}