package pt.isel.ipw.repository.jdbi.activity

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.ipw.domain.Activity
import pt.isel.ipw.repository.ActivityRepository

class JdbiActivityRepository(
    private val handle: Handle
) : ActivityRepository {

    override fun createActivity(
        processId: Int,
        userId: Int,
        action: String,
        description: String
    ): Int {
        return handle.createUpdate(
            """
            insert into Activity(process_id, user_id, action, description)
            values (:processId, :userId, :action, :description)
        """
        )
            .bind("processId", processId)
            .bind("userId", userId)
            .bind("action", action)
            .bind("description", description)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()
    }


    override fun getByProcessId(
        processId: Int,
        offset: Int,
        limit: Int
    ): List<Activity> {
        return handle.createQuery(
            """
                select
                    a.id,
                    a.process_id,
                    a.user_id,
                    u.name as user_name,
                    a.action,
                    a.description,
                    a.created_at
                from Activity a
                join Users u on u.id = a.user_id
                where a.process_id = :processId
                order by a.created_at desc
                limit :limit offset :offset
            """
        )
            .bind("processId", processId)
            .bind("offset", offset)
            .bind("limit", limit)
            .mapTo<Activity>()
            .list()
    }

    override fun getByUserId(
        userId: Int,
        offset: Int,
        limit: Int
    ): List<Activity> {
        return handle.createQuery(
            """
                select
                    a.id,
                    a.process_id,
                    a.user_id,
                    u.name as user_name,
                    a.action,
                    a.description,
                    a.created_at
                from Activity a
                join Users u on u.id = a.user_id
                where a.user_id = :userId
                order by a.created_at desc
                limit :limit offset :offset
            """
        )
            .bind("userId", userId)
            .bind("offset", offset)
            .bind("limit", limit)
            .mapTo<Activity>()
            .list()
    }

}
