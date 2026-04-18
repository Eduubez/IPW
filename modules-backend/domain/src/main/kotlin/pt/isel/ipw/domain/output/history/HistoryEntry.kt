package pt.isel.ipw.domain.output.history

data class UserProcessHistory(
    val userId: Int,
    val process: List<Int>,
)