package pt.isel.ipw.repository.jdbi.history

import org.jdbi.v3.core.Handle
import pt.isel.ipw.domain.HistoryEntryEntity
import pt.isel.ipw.repository.IHistoryRepository

class JdbiHistoryRepository
    (
    private val handle: Handle
) : IHistoryRepository {
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


    private fun roleToColumnMapper(role: String): String {
        return when (role) {
            "triator" -> "triator_id"
            "investigator" -> "investigator_id"
            "supervisor" -> "supervisor_id"
            else -> throw IllegalArgumentException("Invalid role: $role") // for now
        }
    }
}