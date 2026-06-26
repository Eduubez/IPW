package pt.isel.ipw.repository.jdbi.process

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.ipw.domain.process.ProcessView
import pt.isel.ipw.domain.process.Prove
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.repository.ProcessRepository
import pt.isel.ipw.repository.jdbi.mappers.ActivityMapper
import pt.isel.ipw.repository.jdbi.mappers.notes.NoteMapper
import pt.isel.ipw.repository.jdbi.mappers.process.ProcessMapper
import pt.isel.ipw.repository.jdbi.mappers.process.ProvesMapper

class JdbiProcessRepository(
    val handle: Handle
) : ProcessRepository {

    override fun createProcess(
        triatorId: Int,
        name: String,
        street: String,
        county: String,
        district: String,
        latitude: Int?,
        longitude: Int?,
        area: String,
        priority: String,
        expiresAt: String,
        investigatorId: Int?,
        supervisorId: Int?,
        insuranceId: Int?,
        typificationId: Int?,
        canBeFraud: Boolean,
        note: String?
    ): Int {
        val locationId = handle.createUpdate(
            """
            insert into Location(district, county, street, latitude, longitude)
            values (:district, :county, :street, :latitude, :longitude)
            """
        )
            .bind("district", district)
            .bind("county", county)
            .bind("street", street)
            .bind("latitude", latitude)
            .bind("longitude", longitude)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()

        val areaId = handle.createQuery("select id from Area where name = :area")
            .bind("area", area)
            .mapTo(Int::class.java)
            .one()

        val processId = handle.createUpdate(
            """
            insert into Process(name, insurance_id, location, due_date, is_suspect_fraud, priority, area_id, typification_id, triator_id, investigator_id, supervisor_id)
            values (:name, :insuranceId, :locationId, :expiresAt::timestamp, :canBeFraud, :priority, :areaId, :typificationId, :triatorId, :investigatorId, :supervisorId)
            """
        )
            .bind("name", name)
            .bind("insuranceId", insuranceId)
            .bind("locationId", locationId)
            .bind("expiresAt", expiresAt)
            .bind("canBeFraud", canBeFraud)
            .bind("priority", priority)
            .bind("areaId", areaId)
            .bind("typificationId", typificationId)
            .bind("triatorId", triatorId)
            .bind("investigatorId", investigatorId)
            .bind("supervisorId", supervisorId)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()

        // Estado inicial criado automaticamente pelo trigger create_initial_state_for_process

        if (!note.isNullOrBlank()) {
            handle.createUpdate(
                """
                insert into Notes(process_id, content, author_id)
                values (:processId, :content, :authorId)
                """
            )
                .bind("processId", processId)
                .bind("content", note)
                .bind("authorId", triatorId)
                .execute()
        }

        return processId
    }

    override fun getById(id: Int): ProcessView? {
        val notes = handle.createQuery(
            """
        select
            n.id         as id,
            n.process_id as process_id,
            n.proves_id  as proves_id,
            n.content    as content,
            n.author_id  as author_id,
            n.created_at as created_at,
            u.name as author_name
        from Notes n join Users u on n.id = u.id
        where n.process_id = :id
        """
        )
            .bind("id", id)
            .map(NoteMapper())
            .list()

        val activities = handle.createQuery(
            """
        select
            act.id           as activity_id,
            act.process_id   as activity_process_id,
            act.user_id      as activity_user_id,
            uact.name        as activity_user_name,
            act.action       as activity_action,
            act.description  as activity_description,
            act.created_at   as activity_created_at
        from Activity act
        join Users uact on uact.id = act.user_id
        where act.process_id = :id
        """
        )
            .bind("id", id)
            .map(ActivityMapper("activity_"))
            .list()

        val proves = handle.createQuery(
            """
            select
                id,
                process_id,
                file_name,
                content_type,
                file_size,
                storage_key,
                created_by,
                created_at
            from Proves
            where process_id = :processId
            order by created_at desc
            """
        )
            .bind("processId", id)
            .mapTo<Prove>()
            .list()

        return handle.createQuery(
            """
        select
            p.id,
            p.name,
            p.creation_date,
            p.due_date,
            p.priority,

            l.id             as location_id,
            l.district       as location_district,
            l.county         as location_county,
            l.street         as location_street,
            l.latitude       as location_latitude,
            l.longitude      as location_longitude,

            a.id             as area_id,
            a.name           as area_name,
            a.boss_id        as area_boss_id,
            ub.name          as area_boss_name,

            t.id             as typification_id,
            t.name           as typification_name,
            t.honorary       as typification_honorary,

            ut.id            as triator_id,
            ut.name          as triator_name,
            ut.email         as triator_email,
            ut.password_hash as triator_password_hash,
            ut.is_active     as triator_is_active,
            at.name          as triator_area,

            ui.id            as investigator_id,
            ui.name          as investigator_name,
            ui.email         as investigator_email,
            ui.password_hash as investigator_password_hash,
            ui.is_active     as investigator_is_active,
            ai.name          as investigator_area,

            us.id            as supervisor_id,
            us.name          as supervisor_name,
            us.email         as supervisor_email,
            us.password_hash as supervisor_password_hash,
            us.is_active     as supervisor_is_active,
            asuper.name      as supervisor_area,

            cur_state.name   as state_,

            r.id             as report_id,
            r.process_id     as report_process_id,
            r.content        as report_content,
            r.created_at     as report_created_at,
            r.updated_at     as report_updated_at

        from Process p
        join Location l         on p.location        = l.id
        join Area a             on p.area_id         = a.id
        left join Users ub      on a.boss_id         = ub.id
        join Typification t     on p.typification_id = t.id
        join Users ut           on p.triator_id      = ut.id
        left join Area at       on ut.area_id        = at.id
        left join Users ui      on p.investigator_id = ui.id
        left join Area ai       on ui.area_id        = ai.id
        left join Users us      on p.supervisor_id   = us.id
        left join Area asuper   on us.area_id        = asuper.id
        left join (
            select ps.process_id, st.name
            from Process_State ps
            join State st on st.id = ps.state_id
            where ps.end_date is null
        ) cur_state             on cur_state.process_id = p.id
        left join Report r      on r.process_id      = p.id
        where p.id = :id
        limit 1
        """
        )
            .bind("id", id)
            .map(ProcessMapper(notes, activities, proves))
            .findOne()
            .orElse(null)
    }

    override fun getAll(
        offset: Int,
        limit: Int,
        areaId: Int,
        userId: Int,
        role: String,
        processStates: List<String>
    ): List<ProcessView> {
        val userCondition = when (role) {
            Roles.MANAGER    -> "true"
            Roles.INVESTIGATOR -> "p.investigator_id = :userId"
            Roles.SUPERVISOR   -> "p.supervisor_id   = :userId"
            Roles.TRIATOR      -> "p.triator_id      = :userId"
            else               -> "false"
        }

        val processIds = handle.createQuery(
            """
select p.id
from Process p
left join Process_State ps on ps.process_id = p.id and ps.end_date is null
left join State st         on st.id = ps.state_id
where ($userCondition)
and (:areaId = 0 or p.area_id = :areaId)
and (:hasStates = false or st.name = any(:processStates))
order by p.creation_date desc
limit :limit offset :offset
"""
        )
            .bind("userId", userId)
            .bind("areaId", areaId)
            .bind("limit", limit)
            .bind("offset", offset)
            .bind("hasStates", processStates.isNotEmpty())
            .bind("processStates", processStates.toTypedArray())
            .mapTo(Int::class.java)
            .list()

        if (processIds.isEmpty()) return emptyList()

        val allNotes = handle.createQuery(
            """
select
    n.id         as id,
    n.process_id as process_id,
    n.proves_id  as proves_id,
    n.content    as content,
    n.author_id  as author_id,
    n.created_at as created_at,
    u.name as author_name
from Notes n join Users u on n.id = u.id
where n.process_id = any(:ids)
"""
        )
            .bind("ids", processIds.toTypedArray())
            .map(NoteMapper())
            .list()
            .groupBy { it.processId }

        val allActivities = handle.createQuery(
            """
select
    act.id           as activity_id,
    act.process_id   as activity_process_id,
    act.user_id      as activity_user_id,
    uact.name        as activity_user_name,
    act.action       as activity_action,
    act.description  as activity_description,
    act.created_at   as activity_created_at
from Activity act
join Users uact on uact.id = act.user_id
where act.process_id = any(:ids)
"""
        )
            .bind("ids", processIds.toTypedArray())
            .map(ActivityMapper("activity_"))
            .list()
            .groupBy { it.processId }

        val allProves = handle.createQuery(
            """
select 
    pv.id             as proves_id,
    pv.process_id     as proves_process_id,
    pv.file_name      as proves_file_name,
    pv.content_type   as proves_content_type,
    pv.file_size      as proves_file_size,
    pv.storage_key    as proves_storage_key,
    pv.created_by     as proves_created_by,
    pv.created_at     as proves_created_at
from Proves pv
where pv.process_id = any(:ids)
"""
        )
            .bind("ids", processIds.toTypedArray())
            .map(ProvesMapper("proves_"))
            .list()
            .groupBy { it.processId }

        return handle.createQuery(
            """
select
    p.id,
    p.name,
    p.creation_date,
    p.due_date,
    p.priority,

    l.id             as location_id,
    l.district       as location_district,
    l.county         as location_county,
    l.street         as location_street,
    l.latitude       as location_latitude,
    l.longitude      as location_longitude,

    a.id             as area_id,
    a.name           as area_name,
    a.boss_id        as area_boss_id,
    ub.name          as area_boss_name,

    t.id             as typification_id,
    t.name           as typification_name,
    t.honorary       as typification_honorary,

    ut.id            as triator_id,
    ut.name          as triator_name,
    ut.email         as triator_email,
    ut.password_hash as triator_password_hash,
    ut.is_active     as triator_is_active,
    at.name          as triator_area,

    ui.id            as investigator_id,
    ui.name          as investigator_name,
    ui.email         as investigator_email,
    ui.password_hash as investigator_password_hash,
    ui.is_active     as investigator_is_active,
    ai.name          as investigator_area,

    us.id            as supervisor_id,
    us.name          as supervisor_name,
    us.email         as supervisor_email,
    us.password_hash as supervisor_password_hash,
    us.is_active     as supervisor_is_active,
    asuper.name      as supervisor_area,

    cur_state.name   as state_,

    r.id             as report_id,
    r.process_id     as report_process_id,
    r.content        as report_content,
    r.created_at     as report_created_at,
    r.updated_at     as report_updated_at

from Process p
join Location l         on p.location        = l.id
join Area a             on p.area_id         = a.id
left join Users ub      on a.boss_id         = ub.id
join Typification t     on p.typification_id = t.id
join Users ut           on p.triator_id      = ut.id
left join Area at       on ut.area_id        = at.id
left join Users ui      on p.investigator_id = ui.id
left join Area ai       on ui.area_id        = ai.id
left join Users us      on p.supervisor_id   = us.id
left join Area asuper   on us.area_id        = asuper.id
left join (
    select ps.process_id, st.name
    from Process_State ps
    join State st on st.id = ps.state_id
    where ps.end_date is null
) cur_state             on cur_state.process_id = p.id
left join Report r      on r.process_id      = p.id
where p.id = any(:ids)
order by p.creation_date desc
"""
        )
            .bind("ids", processIds.toTypedArray())
            .map { rs, ctx ->
                val processId = rs.getInt("id")
                ProcessMapper(
                    notes = allNotes[processId] ?: emptyList(),
                    activities = allActivities[processId] ?: emptyList(),
                    proves = allProves[processId] ?: emptyList()
                ).map(rs, ctx)
            }
            .list()
    }
    override fun updateEndDate(processId: Int, endDate: String) {
        handle.createUpdate(
            """
            update Process
            set due_date = :endDate::timestamp
            where id = :processId
            """
        )
            .bind("endDate", endDate)
            .bind("processId", processId)
            .execute()
    }

    override fun updateProcessInvestigator(processId: Int, investigatorId: Int) {
        // O trigger set_process_assigned_state_when_fully_assigned trata do estado automaticamente
        handle.createUpdate(
            """
            update Process
            set investigator_id = :investigatorId
            where id = :processId
            """
        )
            .bind("investigatorId", investigatorId)
            .bind("processId", processId)
            .execute()
    }

    override fun updateProcessSupervisor(processId: Int, supervisorId: Int) {
        // O trigger set_process_assigned_state_when_fully_assigned trata do estado automaticamente
        handle.createUpdate(
            """
            update Process
            set supervisor_id = :supervisorId
            where id = :processId
            """
        )
            .bind("supervisorId", supervisorId)
            .bind("processId", processId)
            .execute()
    }

    override fun updateProcessPriority(processId: Int, newPriority: String) {
        handle.createUpdate(
            """
            update Process
            set priority = :newPriority
            where id = :processId
            """
        )
            .bind("newPriority", newPriority)
            .bind("processId", processId)
            .execute()
    }

    override fun cancelProcess(processId: Int) {
        val stateId = handle.createQuery(
            "select id from State where name = 'canceled'"
        )
            .mapTo(Int::class.java)
            .one()

        handle.createUpdate(
            """
        insert into Process_State(process_id, state_id, start_date)
        values (:processId, :stateId, current_timestamp)
        """
        )
            .bind("processId", processId)
            .bind("stateId", stateId)
            .execute()
    }


    override fun changeState(processId: Int, newState: String) {
        val stateId = handle.createQuery(
            """
        select id from State where name = :state
        """
        )
            .bind("state", newState)
            .mapTo(Int::class.java)
            .one()

        handle.createUpdate(
            """
        insert into Process_State(process_id, state_id, start_date)
        values (:processId, :stateId, current_timestamp)
        """
        )
            .bind("processId", processId)
            .bind("stateId", stateId)
            .execute()
    }

}
