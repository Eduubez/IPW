package pt.isel.ipw.repository.jdbi.area

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.ipw.domain.Entities.area.AreaView
import pt.isel.ipw.repository.AreasRepository
import pt.isel.ipw.repository.jdbi.mappers.AreaViewMapper

class JdbiAreasRepository(
    private val handle: Handle
) : AreasRepository {

    override fun isAreaStoredById(areaId: Int): Boolean {
        return handle.createQuery(
            """
                select count(*)
                from Area
                where id = :areaId
            """
        )
            .bind("areaId", areaId)
            .mapTo<Int>()
            .one() > 0
    }

    override fun hasBoss(areaId: Int): Boolean {
        return handle.createQuery(
            """
                select count(*)
                from Area
                where id = :areaId
                  and boss_id is not null
            """
        )
            .bind("areaId", areaId)
            .mapTo<Int>()
            .one() > 0
    }

    override fun getBossId(areaId: Int): Int? {
        return handle.createQuery(
            """
                select boss_id
                from Area
                where id = :areaId
            """
        )
            .bind("areaId", areaId)
            .map { rs, _ ->
                val bossId = rs.getInt("boss_id")
                if (rs.wasNull()) null else bossId
            }
            .singleOrNull()
    }

    override fun updateBoss(areaId: Int, userId: Int) {
        handle.createUpdate(
            """
                update Area
                set boss_id = :userId
                where id = :areaId
            """
        )
            .bind("userId", userId)
            .bind("areaId", areaId)
            .execute()
    }

    override fun clearBossByUserId(userId: Int) {
        handle.createUpdate(
            """
                update Area
                set boss_id = null
                where boss_id = :userId
            """
        )
            .bind("userId", userId)
            .execute()
    }

    override fun getAllAreas(): List<AreaView> {
        val query = """
            SELECT area.id, area.name, area.boss_id, Users.name as boss_name
            FROM area LEFT JOIN Users ON area.boss_id = Users.id
        """.trimIndent()
        return handle.createQuery(query)
            .map(AreaViewMapper())
            .list()
    }

    override fun getAreaById(areaId: Int): AreaView? {
        val query = """
            SELECT area.id, area.name, area.boss_id, Users.name as boss_name
            FROM area LEFT JOIN Users ON area.boss_id = Users.id
            WHERE area.id = :areaId
        """.trimIndent()
        return handle.createQuery(query)
            .bind("areaId", areaId)
            .map(AreaViewMapper())
            .findOne()
            .orElse(null)
    }
}
