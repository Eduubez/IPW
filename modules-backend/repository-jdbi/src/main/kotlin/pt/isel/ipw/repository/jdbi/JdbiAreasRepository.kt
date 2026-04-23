package pt.isel.ipw.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.ipw.repository.AreasRepository

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
}