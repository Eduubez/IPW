package pt.isel.ipw.services.interfaces

import com.google.common.math.IntMath
import pt.isel.ipw.services.results.CreateProveResult
import pt.isel.ipw.services.results.CreateProveUploadUrlResult
import pt.isel.ipw.services.results.DeleteProveResult
import pt.isel.ipw.services.results.GetProcessProvesResult
import pt.isel.ipw.services.results.GetProveAccessUrlResult

interface ProveService {

    fun createProve(
        processId: Int,
        userId: Int,
        role: String,
        fileName: String,
        contentType: String,
        fileSize: Long,
        storageKey: String
    ): CreateProveResult

    fun createUploadUrl(
        processId: Int,
        userId: Int,
        role: String,
        fileName: String,
        contentType: String,
        fileSize: Long
    ): CreateProveUploadUrlResult

    fun getProcessProves(
        processId: Int,
        userId: Int,
        role: String
    ): GetProcessProvesResult

    fun getProveAccessUrl(
        processId: Int,
        proveId: Int,
        userId: Int,
        role: String
    ): GetProveAccessUrlResult

    fun deleteProve(
        processId: Int,
        proveId: Int,
        userId: Int,
        role: String
    ): DeleteProveResult
}