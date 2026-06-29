package pt.isel.ipw.services.errors

sealed class ReportError(
    val status: Int,
) {
    data object InvalidContent : ReportError(400)
    data object ProcessNotFound : ReportError(404)
    data object Unauthorized : ReportError(403)
    data object UnauthorizedInvestigator: ReportError(403)
    data object ReportNotFound : ReportError(404)
    data object InvalidState : ReportError(409)
    data object NotAssociated : ReportError(403)
    data object NotApprovedBySupervisor : ReportError(409)
    data object AlreadyRejected : ReportError(409)
    data object AlreadyApproved: ReportError(409)
    data object ProcessFinished: ReportError(400)

}