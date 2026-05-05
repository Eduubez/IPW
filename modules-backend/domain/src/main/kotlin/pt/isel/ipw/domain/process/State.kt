package pt.isel.ipw.domain.process

enum class State {
    NOT_ASSIGNED,
    ASSIGNED,
    ON_GOING,
    WAITING_APPROVAL_SUPERVISOR,
    APPROVED_BY_SUPERVISOR,
    REJECTED_BY_SUPERVISOR,
    WAITING_FOR_APPROVAL_MANAGER,
    APPROVED_BY_MANAGER,
    REJECTED_BY_MANAGER,
    CANCELED
}

fun String.toState(): State {
    return when (this.lowercase()) {
        "not_assigned" -> State.NOT_ASSIGNED
        "assigned" -> State.ASSIGNED
        "on_going" -> State.ON_GOING
        "waiting_approval_supervisor" -> State.WAITING_APPROVAL_SUPERVISOR
        "approved_by_supervisor" -> State.APPROVED_BY_SUPERVISOR
        "rejected_by_supervisor" -> State.REJECTED_BY_SUPERVISOR
        "waiting_for_approval_manager" -> State.WAITING_FOR_APPROVAL_MANAGER
        "approved_by_manager" -> State.APPROVED_BY_MANAGER
        "rejected_by_manager" -> State.REJECTED_BY_MANAGER
        "canceled" -> State.CANCELED
        else -> throw IllegalArgumentException("Unknown state: $this")
    }
}
