package pt.isel.ipw.domain

import java.time.LocalDateTime

data class Activity(
    val id: Int,
    val processId: Int,
    val userId: Int,
    val action: String,
    val description: String?,
    val createdAt: LocalDateTime
){
}