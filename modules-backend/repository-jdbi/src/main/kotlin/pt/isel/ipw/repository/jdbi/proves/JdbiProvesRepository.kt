package pt.isel.ipw.repository.jdbi.proves

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.ipw.domain.process.Prove
import pt.isel.ipw.repository.ProvesRepository

class JdbiProvesRepository(
    private val handle: Handle
) : ProvesRepository {

    override fun create(
        processId: Int,
        fileName: String,
        contentType: String,
        fileSize: Long,
        storageKey: String,
        createdBy: Int
    ): Int =
        handle.createUpdate(
            """
            insert into Proves(process_id, file_name, content_type, file_size, storage_key, created_by)
            values (:processId, :fileName, :contentType, :fileSize, :storageKey, :createdBy)
            """
        )
            .bind("processId", processId)
            .bind("fileName", fileName)
            .bind("contentType", contentType)
            .bind("fileSize", fileSize)
            .bind("storageKey", storageKey)
            .bind("createdBy", createdBy)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun getById(proveId: Int): Prove? =
        handle.createQuery(
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
            where id = :proveId
            """
        )
            .bind("proveId", proveId)
            .mapTo<Prove>()
            .singleOrNull()

    override fun getByProcessId(processId: Int): List<Prove> =
        handle.createQuery(
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
            .bind("processId", processId)
            .mapTo<Prove>()
            .list()

    override fun delete(proveId: Int) {
        handle.createUpdate(
            """
            delete from Proves
            where id = :proveId
            """
        )
            .bind("proveId", proveId)
            .execute()
    }
}