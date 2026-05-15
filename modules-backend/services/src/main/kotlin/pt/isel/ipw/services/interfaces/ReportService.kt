package pt.isel.ipw.services.interfaces

import pt.isel.ipw.domain.report.Report
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.ReportError
import java.time.LocalDateTime

interface ReportService {

    fun createReport(
        processId: Int,
        content: String,
        userId: Int,
    ): Either<ReportError, Int>

    fun getByProcessId(
        processId: Int,
        userId: Int,
        role: String
    ):Either<ReportError, Report>

    fun updateReport(
        processId: Int,
        content: String,
        userId: Int
    ): Either<ReportError, Unit>

     fun deleteReport(
         processId: Int,
         userId: Int,
     ): Either<ReportError, Unit>


     fun approveReport(
         processId: Int,
         userId: Int,
         role: String
     ):Either<ReportError, Unit>

    fun rejectReport(
        processId: Int,
        userId: Int,
        role: String
    ):Either<ReportError, Unit>
}
