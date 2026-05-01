package pt.isel.ipw.services.errors

sealed class ProcessError(
    val code: String,
    val status: Int,
    val message: String
) {
    data object InvalidProcessId : ProcessError("InvalidProcessId", 400, "Process ID must be a positive integer.")
    data object InvalidName : ProcessError("InvalideName", 400, "Process name must have at least 3 characters.")
    data object InvalidLocation : ProcessError("InvalidLocation", 400, "Location must have all fields filled and valid.")
    data object InvalidExpirationDate : ProcessError("InvalidExpirationDate", 400, "The expiration date is not valid.")
    data object InvalidPriority : ProcessError("InvalidPriority", 400, "The priority is not valid.")
    data object InvalidInvestigator : ProcessError("InvalidInvestigator", 400, "Investigator do not exists or belongs to the same area.")
    data object InvalidSupervisor : ProcessError("InvalidSupervisor", 400, "Supervisor do not exists or belongs to the same area.")
    data object InvalidTriator: ProcessError("InvalidTriator", 400, "Triator is not allowed to crate a new process.")
    data object InvalidInsurance: ProcessError("InvalidInsurance", 400, "Insurance does not exist.")
    data object InvalidTypification: ProcessError("InvalidTypification", 400, "Typification does not exist.")
    data object UpdateNotAllowed: ProcessError("UpdateNotAllowed", 401, "Update is not allowed.")
    data object InvalidUserId: ProcessError("InvalidUserId", 400, "Invalid userId.")
    data object ProcessNotFound: ProcessError("ProcessNotFound", 404, "Process not found.")
    data object InvalidLimit: ProcessError("InvalidLimit", 404, "Limit must be positive.")
    data object InvalidOffset: ProcessError("InvalidOffset", 404, "Offset must be positive.")
    data object InvalidAreaId: ProcessError("InvalidAreaId", 404, "Area id must be positive.")
    data object UnauthorizedAccess: ProcessError("UnauthorizedAccess", 403, "User must be associated to this process.")
}