package pt.isel.ipw.repository

import pt.isel.ipw.domain.Entities.area.AreaEntity
import pt.isel.ipw.domain.HistoryEntryEntity


interface HistoryRepository {
    fun getHistoryByUserId(userId: Int, role: String): List<HistoryEntryEntity>
    fun getHistoryByAreaId(areaId: Int): List<Int>
    fun getAreaById(areaId: Int): AreaEntity?
}