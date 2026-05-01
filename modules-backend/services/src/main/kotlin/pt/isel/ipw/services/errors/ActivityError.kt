package pt.isel.ipw.services.errors

sealed class ActivityError(val status: Int) {
    data object InvalidOffset : ActivityError(400)
    data object InvalidLimit : ActivityError(400)
    data object ProcessNotFound : ActivityError(404)
    data object UserNotFound : ActivityError(404)
    data object InvalidAction : ActivityError(400)
    data object InvalidDescription : ActivityError(400)
}