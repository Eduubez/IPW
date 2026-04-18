package pt.isel.ipw.domain.output.history


data class UserHistoryResponse(
    val userId: Int,
    val history: List<Int>
)