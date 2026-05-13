package pt.isel.ipw.services

import org.springframework.stereotype.Service
import pt.isel.ipw.repository.Transaction
import pt.isel.ipw.repository.TransactionManager
import pt.isel.ipw.services.errors.*
import pt.isel.ipw.services.interfaces.ActivityService
import pt.isel.ipw.services.results.ActivityValidationResult
import pt.isel.ipw.services.results.CreateActivityResult
import pt.isel.ipw.services.results.GetActivitiesByProcessResult
import pt.isel.ipw.services.results.GetActivitiesByUserResult

@Service
class ActivityServiceImpl(
    private val transactionManager: TransactionManager
) : ActivityService {

    override fun getActivitiesByProcess(
        processId: Int,
        offset: Int,
        limit: Int
    ): GetActivitiesByProcessResult = transactionManager.run {
        when {
            offset < 0 -> failure(ActivityError.InvalidOffset)
            limit <= 0 -> failure(ActivityError.InvalidLimit)

            // TODO: validar quando ProcessRepository estiver pronto
            // !processRepository.isProcessStoredById(processId) -> failure(ActivityError.ProcessNotFound)

            else -> success(activityRepository.getByProcessId(processId, offset, limit))
        }
    }

    override fun getActivitiesByUser(
        userId: Int,
        offset: Int,
        limit: Int
    ): GetActivitiesByUserResult = transactionManager.run {
        when {
            offset < 0 -> failure(ActivityError.InvalidOffset)
            limit <= 0 -> failure(ActivityError.InvalidLimit)
            !(usersRepository.isUserStoredById(userId)) -> failure(ActivityError.UserNotFound)
            else -> success(activityRepository.getByUserId(userId, offset, limit))
        }
    }

    override fun createActivity(
        processId: Int,
        userId: Int,
        action: String,
        description: String
    ): CreateActivityResult =
        transactionManager.run{
            val validation = validateCreationFields(processId, userId, action, description)

            if (validation is Failure) {
                return@run failure(validation.value)
            }

            val activityId = activityRepository.createActivity(
                processId,
                userId,
                action,
                description
            )

            return@run success(activityId)

        }



    fun Transaction.validateCreationFields(
        processId: Int,
        userId: Int,
        action: String,
        description: String?
    ): ActivityValidationResult {
        if(processRepository.getById(processId) == null) return failure(ActivityError.ProcessNotFound)
        if(userId <= 0) return failure(ActivityError.UserNotFound)
        if(action.isBlank()) return failure(ActivityError.InvalidAction)
        if(description != null && description.isBlank()) return failure(ActivityError.InvalidDescription)
        return success(Unit)
    }

}
