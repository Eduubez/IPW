package pt.isel.ipw.repository

import pt.isel.ipw.domain.HistoryEntryEntity


interface IHistoryRepository {
    fun getHistoryByUserId(userId: Int, role: String): List<HistoryEntryEntity>
    fun getHistoryByAreaId(areaId: Int): List<Int>
}