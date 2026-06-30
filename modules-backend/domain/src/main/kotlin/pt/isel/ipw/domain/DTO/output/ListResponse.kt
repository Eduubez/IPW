package pt.isel.ipw.domain.DTO.output

data class ListResponse<T>(
    val results: List<T>,
    val hasNext : Boolean = false,
    val totalCount : Int? = null
){
}