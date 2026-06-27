package pt.isel.ipw.services.schedulers

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import pt.isel.ipw.services.interfaces.ProcessService

@Component
class ProcessPriorityScheduler(
    private val processService: ProcessService
) {

    @Scheduled(cron = "0 0 3 * * *")
    fun updatePrioritiesByDeadline() {
        processService.updatePrioritiesByDeadline()
            .fold(
                onFailure = { error ->
                    log.warn("Failed to update process priorities by deadline: {}", error)
                },
                onSuccess = { updatedProcesses ->
                    if (updatedProcesses > 0) {
                        log.info("Updated {} process priorities by deadline", updatedProcesses)
                    }
                }
            )
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProcessPriorityScheduler::class.java)
    }
}
