package pt.isel.ipw.domain.output

// Atualmente o dto do processo envia para o frontend apenas os nomes dos envolvidos (triador, etc), pois não existe a necessidade de mais informação,

data class GetProcessResponse(
    val id: Int,
    val name: String,
    val location: LocationResponse,
    val creationDate: String,   //Informação toda, front-end da parse
    val dueDate: String,
    val priority: Int,
    val area: String,
    val typification: String,
    val triatorName: String,
    val investigatorName: String,
    val supervisorName: String,
    val state: String,
    val proves: ProvesResponse,

    //val report: ReportResponse, //se temos um endpoint para report n há necessidade de passar aqui o report
    //val notes: NotesResponse,  // se temos um endpoint para notes ...
    //val activity: ActivityResponse,  // mm coisa

    )


/*

fun Process.toResponse(): ProcessOutputModel = GetProcessResponse()

fun List<Process>.toResponse(): List<ProcessOutputModel> = this.map { it.toResponse() }

*/
