package pt.isel.ipw.domain.process

enum class State {
    NOT_ASSIGNED,
    ASSIGNED,
    ON_GOING,
    WAITING_APPROVAL_SUPERVISOR,
    APPROVED_BY_SUPERVISOR,
    REJECTED_BY_SUPERVISOR,
    WAITING_APPROVAL_MANAGER,
    APPROVED_BY_MANAGER,
    REJECTED_BY_MANAGER,
    CANCELED;


    override fun toString(): String =
        when (this) {
            NOT_ASSIGNED -> "not_assigned"
            ASSIGNED -> "assigned"
            ON_GOING -> "on_going"
            WAITING_APPROVAL_SUPERVISOR -> "waiting_approval_supervisor"
            APPROVED_BY_SUPERVISOR -> "approved_by_supervisor"
            REJECTED_BY_SUPERVISOR -> "rejected_by_supervisor"
            WAITING_APPROVAL_MANAGER -> "waiting_approval_manager"
            APPROVED_BY_MANAGER -> "approved_by_manager"
            REJECTED_BY_MANAGER -> "rejected_by_manager"
            CANCELED -> "canceled"
        }

    companion object {
        fun mapStringToState(value: String): State? =
            when (value.lowercase()) {
                "not_assigned" -> NOT_ASSIGNED
                "assigned" -> ASSIGNED
                "on_going" -> ON_GOING
                "waiting_approval_supervisor" -> WAITING_APPROVAL_SUPERVISOR
                "approved_by_supervisor" -> APPROVED_BY_SUPERVISOR
                "rejected_by_supervisor" -> REJECTED_BY_SUPERVISOR
                "waiting_approval_manager" -> WAITING_APPROVAL_MANAGER
                "approved_by_manager" -> APPROVED_BY_MANAGER
                "rejected_by_manager" -> REJECTED_BY_MANAGER
                "canceled" -> CANCELED
                else -> null
            }
    }


}
