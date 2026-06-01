package pt.isel.ipw.services.errors

sealed class HistoryError(
    val status: Int,
) {
    data object InvalidUserId : HistoryError( 400)
    data object InvalidAreaId : HistoryError( 400)
    data object Forbidden : HistoryError(403)
    data object AreaNotFound : HistoryError( 404)
}