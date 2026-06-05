package pt.isel.ipw.services.errors

sealed class ProcessError(
    val status: Int,
) {
    data object InvalidProcessId : ProcessError(400)
    data object InvalidName : ProcessError(400)
    data object InvalidLocation : ProcessError(400)
    data object InvalidExpirationDate : ProcessError( 400)
    data object InvalidPriority : ProcessError(400)
    data object InvalidInvestigator : ProcessError( 400)
    data object InvalidSupervisor : ProcessError( 400)
    data object InvalidTriator: ProcessError( 400)
    data object InvalidInsurance: ProcessError( 400)
    data object InvalidTypification: ProcessError( 400)
    data object UpdateNotAllowed: ProcessError( 401)
    data object InvalidUserId: ProcessError( 400)
    data object ProcessNotFound: ProcessError( 404)
    data object InvalidLimit: ProcessError( 404)
    data object InvalidOffset: ProcessError( 404)
    data object InvalidAreaId: ProcessError(404)
    data object UnauthorizedAccess: ProcessError(403)
    data object InvalidState: ProcessError(400)
    data object ReportNotFound: ProcessError( 400)
}