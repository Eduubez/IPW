package pt.isel.ipw.domain

enum class ActivityActions {
    CREATED_PROCESS,
    ASSIGNED_SUPERVISOR,
    UPDATED_REPORT,
    CANCELLED_PROCESS,
    CHANGED_PRIORITY,
    CHANGED_END_DATE,
    CREATED_REPORT,
    DELETED_REPORT,
    APPROVED_REPORT_SUPERVISOR,
    REJECTED_REPORT_SUPERVISOR,
    APPROVED_REPORT_MANAGER,
    REJECTED_REPORT_MANAGER,
}


fun ActivityActions.mapToString(): String =
    when (this) {
        ActivityActions.CREATED_PROCESS -> "created a new process"
        ActivityActions.UPDATED_REPORT -> "updated the report"
        ActivityActions.CANCELLED_PROCESS -> "canceled the process"
        ActivityActions.CHANGED_PRIORITY -> "changed the priority"
        ActivityActions.ASSIGNED_SUPERVISOR -> "assigned a supervisor"
        ActivityActions.CHANGED_END_DATE -> "changed the end date"
        ActivityActions.CREATED_REPORT -> "created a new report"
        ActivityActions.DELETED_REPORT -> "deleted a report"
        ActivityActions.APPROVED_REPORT_SUPERVISOR -> "Supervisor approved the report"
        ActivityActions.REJECTED_REPORT_SUPERVISOR -> "Supervisor rejected the report"
        ActivityActions.APPROVED_REPORT_MANAGER -> "Manager approved the report"
        ActivityActions.REJECTED_REPORT_MANAGER -> "Manager rejected the report"
    }






