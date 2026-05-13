package pt.isel.ipw.services.interfaces

import pt.isel.ipw.services.results.GetAllAreasResult
import pt.isel.ipw.services.results.GetAreaByIdResult
import pt.isel.ipw.services.results.UpdateAreaBossResult

interface AreaService{
    fun getAllAreas(): GetAllAreasResult
    fun getAreaById(id: Int): GetAreaByIdResult
    fun updateAreaBoss(id: Int, bossId: Int): UpdateAreaBossResult
}
