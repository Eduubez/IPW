package pt.isel.ipw.domain.process

enum class Priority {
    NORMAL,
    WITH_PRIORITY,
    URGENT;

    companion object {
        fun mapStringToPriority(value: String): Priority? =
            when (value.uppercase()) {
                "NORMAL" ->  NORMAL
                "WITH_PRIORITY" ->  WITH_PRIORITY
                "URGENT" ->  URGENT
                else ->  null
            }

    }
}


