package pt.isel.ipw.services.results

import pt.isel.ipw.domain.Activity
import pt.isel.ipw.services.errors.ActivityError
import pt.isel.ipw.services.errors.Either

typealias GetActivitiesByProcessResult = Either<ActivityError, List<Activity>>
typealias GetActivitiesByUserResult = Either<ActivityError, List<Activity>>
typealias CreateActivityResult = Either<ActivityError, Int>
typealias ActivityValidationResult = Either<ActivityError, Unit>
