package pt.isel.ipw.services.errors

sealed class HistoryError(
    val code: String,
    val status: Int,
    val message: String
) {
    data object InvalidUserId : HistoryError("InvalidUserId", 400, "User ID must be a positive integer")
    data object InvalidAreaId : HistoryError("InvalidAreaId", 400, "Area ID must be a positive integer")
    data object Forbidden : HistoryError("Forbidden", 403, "You do not have permission to access this history")
    data object AreaNotFound : HistoryError("AreaNotFound", 404, "The specified area does not exist")
}