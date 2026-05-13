package pt.isel.ipw.services.interfaces

import pt.isel.ipw.services.results.GetAreaHistoryResult
import pt.isel.ipw.services.results.GetUserHistoryResult

interface HistoryService {
    fun getUserHistory(userId: Int, userRole: String): GetUserHistoryResult
    fun getAreaHistory(subject:Int,areaId: Int): GetAreaHistoryResult

}
