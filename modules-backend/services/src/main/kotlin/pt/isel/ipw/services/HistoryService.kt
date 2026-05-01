package pt.isel.ipw.services

import org.springframework.stereotype.Service
import pt.isel.ipw.domain.HistoryEntryEntity
import pt.isel.ipw.domain.DTO.output.history.AreaProcessHistory
import pt.isel.ipw.domain.DTO.output.history.UserProcessHistory
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.repository.TransactionManager
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.HistoryError
import pt.isel.ipw.services.errors.failure
import pt.isel.ipw.services.errors.success
import pt.isel.ipw.services.interfaces.IHistoryService

typealias HistoryResponse = Either<HistoryError, UserProcessHistory>

@Service
class HistoryService(
    private val transactionManager: TransactionManager,

    ) : IHistoryService {
    override fun getUserHistory(userId: Int,userRole:String): HistoryResponse {
        val canBeUser = validateUserId(userId)
        val possibleRoles = listOf(Roles.INVESTIGATOR, Roles.SUPERVISOR, Roles.TRIATOR)


        if(!possibleRoles.contains(userRole)){ // Asside from those 3 roles , theres no history
            return success(UserProcessHistory(userId, listOf()))
        }

        if (!canBeUser) {
            return failure(HistoryError.InvalidUserId)
        }

        return transactionManager.run {
            val historyEntries = historyRepository.getHistoryByUserId(userId, userRole)
            success(mapTopOutput(historyEntries,userId))
        }
    }

    override fun getAreaHistory(subject:Int,areaId: Int): Either<HistoryError, AreaProcessHistory> {
        val canBeArea = validateAreaId(areaId)
        if (!canBeArea) {
            return failure(HistoryError.InvalidAreaId)
        }

        return transactionManager.run {
            val area = historyRepository.getAreaById(areaId) ?: return@run failure(HistoryError.AreaNotFound)

            if (area.bossId != subject) {
                return@run failure(HistoryError.Forbidden)
            }
            val areaHistory = historyRepository.getHistoryByAreaId(areaId)
            success(AreaProcessHistory(areaId, areaHistory))
        }
    }

    private fun mapTopOutput(historyEntries: List<HistoryEntryEntity>,userId:Int): UserProcessHistory {
        val processes = historyEntries.map { it.processId }
        return (UserProcessHistory(userId, processes))
    }
    private fun validateUserId(userId: Int): Boolean {
        return userId > 0
    }
    private fun validateAreaId(areaId: Int): Boolean {
        return areaId > 0
    }
}