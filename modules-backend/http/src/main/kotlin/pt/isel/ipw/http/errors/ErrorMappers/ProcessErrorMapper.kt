package pt.isel.ipw.http.errors

import pt.isel.ipw.services.errors.ProcessError

private val processErrorMap = mapOf(
    ProcessError.InvalidName to Problem.invalidProcessName,
    ProcessError.InvalidLocation to Problem.invalidLocation,
    ProcessError.InvalidExpirationDate to Problem.invalidExpirationDate,
    ProcessError.InvalidPriority to Problem.invalidPriority,
    ProcessError.InvalidInvestigator to Problem.invalidInvestigator,
    ProcessError.InvalidSupervisor to Problem.invalidSupervisor,
    ProcessError.InvalidTriator to Problem.invalidTriator,
    ProcessError.InvalidProcessId to Problem.processNotFound,
    ProcessError.InvalidInsurance to Problem.invalidInsurance,
    ProcessError.InvalidTypification to Problem.invalidTypification,
    ProcessError.UpdateNotAllowed to Problem.updateNotAllowed,
    ProcessError.InvalidUserId to Problem.invalidUserId,
    ProcessError.ProcessNotFound to Problem.processNotFound,
    ProcessError.InvalidLimit to Problem.invalidLimit,
    ProcessError.InvalidOffset to Problem.invalidOffset,
    ProcessError.InvalidAreaId to Problem.invalidAreaId,
    ProcessError.ReportNotFound to Problem.reportNotFound,
)

fun ProcessError.toHttp(): Pair<Int, Problem> =
    this.status to (processErrorMap[this] ?: Problem.internalServerError)
