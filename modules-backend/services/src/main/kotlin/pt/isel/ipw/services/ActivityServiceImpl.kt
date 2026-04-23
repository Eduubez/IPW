package pt.isel.ipw.services

import org.springframework.stereotype.Service
import pt.isel.ipw.domain.Activity
import pt.isel.ipw.repository.TransactionManager
import pt.isel.ipw.services.errors.ActivityError
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.failure
import pt.isel.ipw.services.errors.success
import pt.isel.ipw.services.interfaces.ActivityService

@Service
class ActivityServiceImpl(
    private val transactionManager: TransactionManager
) : ActivityService {

    override fun getActivitiesByProcess(
        processId: Int,
        offset: Int,
        limit: Int
    ): Either<ActivityError, List<Activity>> = transactionManager.run {
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
    ): Either<ActivityError, List<Activity>> = transactionManager.run {
        when {
            offset < 0 -> failure(ActivityError.InvalidOffset)
            limit <= 0 -> failure(ActivityError.InvalidLimit)
            !(usersRepository.isUserStoredById(userId)) -> failure(ActivityError.UserNotFound)
            else -> success(activityRepository.getByUserId(userId, offset, limit))
        }
    }
}