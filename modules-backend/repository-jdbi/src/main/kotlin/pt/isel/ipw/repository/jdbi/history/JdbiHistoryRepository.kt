package pt.isel.ipw.repository.jdbi.history

import org.jdbi.v3.core.Handle
import pt.isel.ipw.domain.Entities.area.AreaEntity
import pt.isel.ipw.domain.HistoryEntryEntity
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.repository.HistoryRepository

class JdbiHistoryRepository
    (
    private val handle: Handle
) : HistoryRepository {
    override fun getHistoryByUserId(userId: Int, role: String): List<HistoryEntryEntity> {
        val userColumn = roleToColumnMapper(role)
        val query = """
            SELECT id as processId, $userColumn as userId
            FROM Process
            WHERE $userColumn = :userId
        """.trimIndent()

        return handle.createQuery(query)
            .bind("userId", userId)
            .mapTo(HistoryEntryEntity::class.java)
            .list()
    }

    override fun getHistoryByAreaId(areaId: Int): List<Int> {
        val query = """
            SELECT id as processId
            FROM Process
            WHERE area_id = :areaId
        """.trimIndent()

        return handle.createQuery(query)
            .bind("areaId", areaId)
            .mapTo(Int::class.java)
            .list()
    }
    override fun getAreaById(areaId: Int): AreaEntity? {
        val query = """
            SELECT id, name, boss_id
            FROM Area
            WHERE id = :areaId
        """.trimIndent()

        return handle.createQuery(query)
            .bind("areaId", areaId)
            .mapTo(AreaEntity::class.java)
            .findOne()
            .orElse(null)
    }


    private fun roleToColumnMapper(role: String): String {
        return when (role) {
            Roles.TRIATOR -> "triator_id"
            Roles.INVESTIGATOR -> "investigator_id"
            Roles.SUPERVISOR -> "supervisor_id"
            else -> throw IllegalArgumentException("Invalid role: $role")
        }
    }
}