package pt.isel.ipw.domain.DTO.output

data class ListResponse<T>(
    val results: List<T>,
){
}