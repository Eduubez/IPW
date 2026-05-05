package pt.isel.ipw.domain.process

enum class Priority {
    NORMAL,
    WITH_PRIORITY,
    URGENT,
}


fun String.toPriority(): Priority {
    when (this.uppercase()) {
        "NORMAL" -> return Priority.NORMAL
        "WITH_PRIORITY" -> return Priority.WITH_PRIORITY
        "URGENT" -> return Priority.URGENT
    }
    throw IllegalArgumentException("Unknown priority")

}

