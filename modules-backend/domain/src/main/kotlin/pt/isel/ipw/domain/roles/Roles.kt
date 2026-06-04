package pt.isel.ipw.domain.roles

import pt.isel.ipw.domain.process.State

object Roles {
    const val INVESTIGATOR = "investigator"
    const val SUPERVISOR = "supervisor"
    const val TRIATOR = "triator"
    const val MANAGER = "manager"
    const val ADMIN = "admin"

    val ALL = setOf(INVESTIGATOR, SUPERVISOR, TRIATOR, MANAGER, ADMIN)
    val AREA_ROLES = setOf(INVESTIGATOR, SUPERVISOR)
    val AREALESS_ROLES = setOf(ADMIN, TRIATOR)
}