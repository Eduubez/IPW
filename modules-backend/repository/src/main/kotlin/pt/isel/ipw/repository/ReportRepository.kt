package pt.isel.ipw.repository

import pt.isel.ipw.domain.report.Report

interface ReportRepository {

    fun createReport(reportId: Int, content: String): Int
    fun getByProcessId(processId: Int): Report?
    fun updateReport(reportId: Int, content: String)
    fun deleteReport(reportId: Int)

}