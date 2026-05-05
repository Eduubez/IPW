package pt.isel.ipw.domain

enum class ActivityActions {
    CREATED, ASSIGNED_SUPERVISOR, UPDATED, CANCELLED, CHANGED_PRIORITY, CHANGED_END_DATE
}


fun ActivityActions.mapToString(): String =
    when (this) {
        ActivityActions.CREATED -> "created"
        ActivityActions.UPDATED -> "updated"
        ActivityActions.CANCELLED -> "canceled"
        ActivityActions.CHANGED_PRIORITY -> "changed the priority"
        ActivityActions.ASSIGNED_SUPERVISOR -> "assigned a supervisor"
        ActivityActions.CHANGED_END_DATE -> "changed the end date"
    }
