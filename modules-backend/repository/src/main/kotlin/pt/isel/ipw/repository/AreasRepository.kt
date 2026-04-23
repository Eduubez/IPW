package pt.isel.ipw.repository

interface AreasRepository {
    fun isAreaStoredById(areaId: Int): Boolean
    fun hasBoss(areaId: Int): Boolean
    fun updateBoss(areaId: Int, userId: Int)
}