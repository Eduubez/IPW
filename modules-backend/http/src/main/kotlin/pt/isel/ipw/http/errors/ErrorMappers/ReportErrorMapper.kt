package pt.isel.ipw.http.errors

import pt.isel.ipw.services.errors.ReportError

private val reportErrorMap = mapOf(
    ReportError.InvalidContent to Problem.invalidContent,
    ReportError.ProcessNotFound to Problem.processNotFound,
    ReportError.ReportNotFound to Problem.reportNotFound,
    ReportError.NotAssociated to Problem.notAssociated,
    ReportError.UnauthorizedInvestigator to Problem.unauthorizedInvestigator,
    ReportError.Unauthorized to Problem.unauthorized,
    ReportError.AlreadyRejected to Problem.alreadyRejected,
    ReportError.NotApprovedBySupervisor to Problem.notApprovedBySupervisor,
    ReportError.AlreadyApproved to Problem.alreadyApproved,
    ReportError.ProcessFinished to Problem.processFinished,
)

fun ReportError.toHttp(): Pair<Int, Problem> =
    this.status to (reportErrorMap[this] ?: Problem.internalServerError)
