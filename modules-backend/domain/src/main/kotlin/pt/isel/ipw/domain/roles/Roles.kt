package pt.isel.ipw.domain.roles

object Roles {
    const val INVESTIGATOR = "INVESTIGATOR"
    const val SUPERVISOR = "SUPERVISOR"
    const val TRIATOR = "TRIATOR"
    const val MANAGER = "MANAGER"
    const val ADMIN = "ADMIN"

    val ALL = setOf(INVESTIGATOR, SUPERVISOR, TRIATOR, MANAGER, ADMIN)
    val AREA_ROLES = setOf(INVESTIGATOR, SUPERVISOR)
    val AREALESS_ROLES = setOf(ADMIN, TRIATOR)
}