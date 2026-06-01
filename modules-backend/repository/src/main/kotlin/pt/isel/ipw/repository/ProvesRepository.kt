package pt.isel.ipw.repository

import pt.isel.ipw.domain.process.Prove

interface ProvesRepository {
    fun create(
        processId: Int,
        fileName: String,
        contentType: String,
        fileSize: Long,
        storageKey: String,
        createdBy: Int
    ): Int

    fun getById(proveId: Int): Prove?

    fun getByProcessId(processId: Int): List<Prove>

    fun delete(proveId: Int)
}