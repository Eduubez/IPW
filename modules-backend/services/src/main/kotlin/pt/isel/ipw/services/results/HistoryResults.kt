package pt.isel.ipw.services.results

import pt.isel.ipw.domain.DTO.output.history.AreaProcessHistory
import pt.isel.ipw.domain.DTO.output.history.UserProcessHistory
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.HistoryError

typealias GetUserHistoryResult = Either<HistoryError, UserProcessHistory>
typealias GetAreaHistoryResult = Either<HistoryError, AreaProcessHistory>
