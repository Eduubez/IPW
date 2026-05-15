package pt.isel.ipw.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.ipw.domain.report.Report
import pt.isel.ipw.repository.ReportRepository
import pt.isel.ipw.repository.jdbi.mappers.process.ReportMapper

class JdbiReportRepository(
    private val handle: Handle,
): ReportRepository {
    override fun createReport(reportId: Int, content: String): Int {
        return handle.createUpdate(
            """
            INSERT INTO report (process_id, content) VALUES (:reportId, :content)
            """.trimIndent()
        )
        .bind("reportId", reportId)
        .bind("content", content)
        .executeAndReturnGeneratedKeys()
        .mapTo(Int::class.java)
        .one()
    }


    override fun getByProcessId(processId: Int): Report? {
        return handle.createQuery(
            """
            select
                r.id         as id,
                r.process_id as process_id,
                r.content    as content,
                r.created_at as created_at,
                r.updated_at as updated_at
            from Report r
            where r.process_id = :processId
            """
        )
            .bind("processId", processId)
            .map(ReportMapper())
            .findOne()
            .orElse(null)
    }

    override fun updateReport(reportId: Int, content: String) {
        handle.createUpdate(
            """
        update Report
        set content = :content
        where id = :reportId
        """
        )
            .bind("content", content)
            .bind("reportId", reportId)
            .execute()
    }

    override fun deleteReport(reportId: Int) {
        handle.createUpdate(
            """
        delete from Report
        where id = :reportId
        """
        )
            .bind("reportId", reportId)
            .execute()
    }
}