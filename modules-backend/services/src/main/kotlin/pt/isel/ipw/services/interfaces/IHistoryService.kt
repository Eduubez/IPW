package pt.isel.ipw.services.interfaces

import pt.isel.ipw.domain.output.history.AreaProcessHistory
import pt.isel.ipw.domain.output.history.UserProcessHistory
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.HistoryError

interface IHistoryService {
    fun getUserHistory(userId: Int, userRole: String): Either<HistoryError, UserProcessHistory>
    fun getAreaHistory(areaId: Int): Either<HistoryError, AreaProcessHistory>

}