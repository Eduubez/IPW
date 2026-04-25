package pt.isel.ipw.services

import org.springframework.stereotype.Service
import pt.isel.ipw.domain.DTO.output.area.AreaListResponse
import pt.isel.ipw.domain.DTO.output.area.AreaViewResponse
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.repository.TransactionManager
import pt.isel.ipw.services.errors.AreaError
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.failure
import pt.isel.ipw.services.errors.success
import pt.isel.ipw.services.interfaces.AreaService



@Service
class AreaServiceImpl(
    private val transactionManager: TransactionManager,
) : AreaService {
    override fun getAllAreas(): Either<AreaError, AreaListResponse> {

        return transactionManager.run {
            val areas = areasRepository.getAllAreas()
            success(AreaListResponse(areas))
        }
    }

    override fun getAreaById(id: Int): Either<AreaError, AreaViewResponse> {
        if (!canBeArea(id)) {
            return failure(AreaError.InvalidAreaId)
        }
        return transactionManager.run {
            val area = areasRepository.getAreaById(id) ?: return@run failure(AreaError.AreaNotFound)
            success(AreaViewResponse(area.id, area.name, area.bossId, area.bossName))
        }
    }

    override fun updateAreaBoss(id: Int, bossId: Int): Either<AreaError, AreaViewResponse> {
        if(!canBeArea(id)){
            return failure(AreaError.InvalidAreaId)
        }
        if(!canBeUser(bossId)){
            return failure(AreaError.InvalidUserId)
        }
        return transactionManager.run {
            val area = areasRepository.getAreaById(id) ?: return@run failure(AreaError.AreaNotFound)
            val user = usersRepository.getUserById(bossId) ?: return@run failure(AreaError.UserNotFound)
            val alreadySupervisor = usersRepository.getUserRoles(bossId).any { it === Roles.SUPERVISOR }

            val updatedArea = area.copy(bossId = bossId, bossName = user.name)
            areasRepository.updateBoss(area.id, bossId)

            // If the new boss is not already a supervisor, assign them the supervisor role
            if (!alreadySupervisor) {
                usersRepository.addUserRole(bossId, Roles.SUPERVISOR)
            }


            success(AreaViewResponse(updatedArea.id, updatedArea.name, updatedArea.bossId, updatedArea.bossName))
        }
    }

    private fun canBeUser(id: Int): Boolean {
        return id > 0
    }


    private fun canBeArea(id: Int): Boolean {
        return id > 0
    }
}