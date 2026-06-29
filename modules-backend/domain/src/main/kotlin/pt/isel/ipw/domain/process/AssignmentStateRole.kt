package pt.isel.ipw.domain.process

import pt.isel.ipw.domain.roles.Roles

object AssignmentStateRole {

    private val TRIATOR_STATES = setOf(State.NOT_ASSIGNED)

    private val INVESTIGATOR_STATES = setOf(
        State.ASSIGNED,
        State.ON_GOING,
        State.REJECTED_BY_SUPERVISOR,
        State.REJECTED_BY_MANAGER
    )

    private val SUPERVISOR_STATES = setOf(
        State.WAITING_APPROVAL_SUPERVISOR,
    )

    private val MANAGER_STATES = setOf(State.WAITING_APPROVAL_MANAGER)

    fun getAllStates() = TRIATOR_STATES + INVESTIGATOR_STATES + SUPERVISOR_STATES + MANAGER_STATES

    fun getHistoryStates() = setOf( State.APPROVED_BY_MANAGER, State.CANCELED)

    fun getStates(role: String): Set<State> {
        return when (role) {
            Roles.TRIATOR -> TRIATOR_STATES
            Roles.INVESTIGATOR -> INVESTIGATOR_STATES
            Roles.SUPERVISOR -> SUPERVISOR_STATES
            Roles.MANAGER -> MANAGER_STATES
            else -> emptySet()
        }
    }

}
