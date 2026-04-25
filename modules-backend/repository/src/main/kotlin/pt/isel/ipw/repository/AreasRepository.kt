package pt.isel.ipw.repository

import pt.isel.ipw.domain.Entities.area.AreaView

interface AreasRepository {
    fun isAreaStoredById(areaId: Int): Boolean
    fun hasBoss(areaId: Int): Boolean
    fun updateBoss(areaId: Int, userId: Int)
    fun getAllAreas(): List<AreaView>
    fun getAreaById(areaId: Int): AreaView?
}