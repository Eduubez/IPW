package pt.isel.ipw.repository

import pt.isel.ipw.domain.Activity

interface ActivityRepository {

    fun createActivity(
        processId: Int,
        userId: Int,
        action: String,
        description: String
    ):  Int

    fun getByProcessId(processId: Int, offset: Int, limit: Int): List<Activity>

    fun getByUserId(userId: Int, offset: Int, limit: Int): List<Activity>
}