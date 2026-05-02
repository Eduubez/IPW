package pt.isel.ipw.services.interfaces

import pt.isel.ipw.domain.DTO.output.history.AreaProcessHistory
import pt.isel.ipw.domain.DTO.output.history.UserProcessHistory
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.HistoryError

interface HistoryService {
    fun getUserHistory(userId: Int, userRole: String): Either<HistoryError, UserProcessHistory>
    fun getAreaHistory(subject:Int,areaId: Int): Either<HistoryError, AreaProcessHistory>

}