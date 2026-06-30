package pt.isel.ipw.services.results

import pt.isel.ipw.domain.ListProps
import pt.isel.ipw.domain.process.ProcessView
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.ProcessError

typealias CreateProcessResult = Either<ProcessError, Int>
typealias ProcessValidationResult = Either<ProcessError, Unit>
typealias GetProcessResult = Either<ProcessError, ProcessView>
typealias GetAllProcessesResult = Either<ProcessError, Pair<List<ProcessView>, ListProps>>
typealias ChangeEndDateResult = Either<ProcessError, Unit>
typealias AssignInvestigatorResult = Either<ProcessError, Unit>
typealias AssignSupervisorResult = Either<ProcessError, Unit>
typealias ChangePriorityResult = Either<ProcessError, Unit>
typealias UpdateProcessPrioritiesResult = Either<ProcessError, Int>
typealias CancelProcessResult = Either<ProcessError, Unit>
typealias SubmitProcessResult = Either<ProcessError, Unit>
typealias CheckAreaIdResult = Either<ProcessError, Int?>
