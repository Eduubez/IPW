package pt.isel.ipw.domain.DTO.output.history


data class UserHistoryResponse(
    val userId: Int,
    val history: List<Int>
)