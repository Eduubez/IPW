package pt.isel.ipw.domain.DTO.output.history

data class UserProcessHistory(
    val userId: Int,
    val process: List<Int>,
)