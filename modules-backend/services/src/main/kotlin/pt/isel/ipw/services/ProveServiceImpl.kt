package pt.isel.ipw.services

import org.springframework.stereotype.Service
import pt.isel.ipw.domain.DTO.output.prove.CreateProveUploadUrlResponse
import pt.isel.ipw.domain.DTO.output.prove.ProveAccessUrlResponse
import pt.isel.ipw.domain.process.ProcessView
import pt.isel.ipw.domain.process.State
import pt.isel.ipw.domain.roles.Roles
import pt.isel.ipw.repository.TransactionManager
import pt.isel.ipw.services.errors.ProveError
import pt.isel.ipw.services.errors.failure
import pt.isel.ipw.services.errors.success
import pt.isel.ipw.services.interfaces.ProveService
import pt.isel.ipw.services.interfaces.ProveStorageService
import pt.isel.ipw.services.results.CreateProveResult
import pt.isel.ipw.services.results.CreateProveUploadUrlResult
import pt.isel.ipw.services.results.DeleteProveResult
import pt.isel.ipw.services.results.GetProcessProvesResult
import pt.isel.ipw.services.results.GetProveAccessUrlResult
import java.util.UUID

@Service
class ProveServiceImpl(
    private val transactionManager: TransactionManager,
    private val proveStorageService: ProveStorageService,
) : ProveService {

    companion object {
        private const val MAX_FILE_NAME_LENGTH = 255
        private const val MAX_FILE_SIZE = 100L * 1024L * 1024L
        private const val MAX_STORAGE_KEY_LENGTH = 500

        private val ALLOWED_CONTENT_TYPES = setOf(
            "image/jpeg",
            "image/png",
            "image/webp",
            "video/mp4",
            "video/webm",
            "video/quicktime",
            "application/pdf"
        )
    }

    override fun createUploadUrl(
        processId: Int,
        userId: Int,
        role: String,
        fileName: String,
        contentType: String,
        fileSize: Long
    ): CreateProveUploadUrlResult = transactionManager.run {
            val process = processRepository.getById(processId)
                ?: return@run failure(ProveError.ProcessNotFound)

            val error = validateProve(process, userId, role, fileName, contentType, fileSize)
            error?.let { return@run failure(it) }

            val storageKey = createStorageKey(processId, fileName)

            val uploadUrl = try {
                proveStorageService.createUploadUrl(storageKey,fileName)
            } catch (_: Exception) {
                return@run failure(ProveError.StorageError)
            }

            success(
                CreateProveUploadUrlResponse(
                    uploadUrl = uploadUrl,
                    storageKey = storageKey,
                )
            )
        }

    override fun createProve(
        processId: Int,
        userId: Int,
        role: String,
        fileName: String,
        contentType: String,
        fileSize: Long,
        storageKey: String
    ): CreateProveResult = transactionManager.run {
        val process = processRepository.getById(processId)
            ?: return@run failure(ProveError.ProcessNotFound)

        val error = validateProve(
            process = process,
            userId = userId,
            role = role,
            fileName = fileName,
            contentType = contentType,
            fileSize = fileSize,
        )

        error?.let { return@run failure(it) }

        if (!isValidStorageKey(processId, storageKey)) {
            return@run failure(ProveError.InvalidStorageKey)
        }

        val objectExists = try {
            proveStorageService.objectExists(storageKey)
        } catch (_: Exception) {
            return@run failure(ProveError.StorageError)
        }

        if (!objectExists) {
            return@run failure(ProveError.StorageError)
        }

        val proveId = provesRepository.create(
            processId = processId,
            fileName = fileName,
            contentType = contentType,
            fileSize = fileSize,
            storageKey = storageKey,
            createdBy = userId,
        )

        success(proveId)
    }

    override fun getProcessProves(
        processId: Int,
        userId: Int,
        role: String
    ): GetProcessProvesResult = transactionManager.run {
        val process = processRepository.getById(processId)
            ?: return@run failure(ProveError.ProcessNotFound)

        if (!canAccessProves(process, userId, role)) {
            return@run failure(ProveError.UnauthorizedAccess)
        }

        val proves = provesRepository.getByProcessId(processId)

        success(proves)
    }

    override fun getProveAccessUrl(
        processId: Int,
        proveId: Int,
        userId: Int,
        role: String
    ): GetProveAccessUrlResult = transactionManager.run {
        val process = processRepository.getById(processId)
            ?: return@run failure(ProveError.ProcessNotFound)

        if (!canAccessProves(process, userId, role)) {
            return@run failure(ProveError.UnauthorizedAccess)
        }

        val prove = provesRepository.getById(proveId)
            ?: return@run failure(ProveError.ProveNotFound)

        if (prove.processId != processId) {
            return@run failure(ProveError.ProveNotFound)
        }

        val accessUrl = try {
            proveStorageService.createAccessUrl(prove.storageKey,prove.fileName)
        } catch (_: Exception) {
            return@run failure(ProveError.StorageError)
        }

        success(
            ProveAccessUrlResponse(
                url = accessUrl,
                contentType = prove.contentType,
                fileName = prove.fileName,
            )
        )
    }

    override fun deleteProve(
        processId: Int,
        proveId: Int,
        userId: Int,
        role: String
    ): DeleteProveResult = transactionManager.run {
        val process = processRepository.getById(processId)
            ?: return@run failure(ProveError.ProcessNotFound)

        if (!canAccessProves(process, userId, role)) {
            return@run failure(ProveError.UnauthorizedAccess)
        }

        val prove = provesRepository.getById(proveId)
            ?: return@run failure(ProveError.ProveNotFound)

        if (prove.processId != processId) {
            return@run failure(ProveError.ProveNotFound)
        }

        try {
            proveStorageService.deleteObject(prove.storageKey)
        } catch (_: Exception) {
            return@run failure(ProveError.StorageError)
        }

        provesRepository.delete(proveId)

        success(Unit)
    }

    private fun canAccessProves(
        process: ProcessView,
        userId: Int,
        role: String
    ): Boolean =
        when (process.state) {
            State.ASSIGNED,
            State.ON_GOING,
            State.REJECTED_BY_SUPERVISOR ->
                role == Roles.INVESTIGATOR && process.investigator?.id == userId

            State.WAITING_APPROVAL_SUPERVISOR,
            State.REJECTED_BY_MANAGER ->
                role == Roles.SUPERVISOR && process.supervisor?.id == userId

            State.WAITING_APPROVAL_MANAGER ->
                role == Roles.MANAGER

            else -> false
        }


    private fun validateProve(
        process: ProcessView,
        userId: Int,
        role: String,
        fileName: String,
        contentType: String,
        fileSize: Long,
    ): ProveError? =
        when {
            !canAccessProves(process, userId, role) -> ProveError.UnauthorizedAccess
            !isValidFileName(fileName) -> ProveError.InvalidFileName
            !isValidContentType(contentType) -> ProveError.InvalidContentType
            !isValidFileSize(fileSize) -> ProveError.InvalidFileSize
            else -> null
        }

    private fun isValidStorageKey(processId: Int, storageKey: String): Boolean =
        storageKey.isNotBlank() &&
                storageKey.startsWith("processes/$processId/") &&
                storageKey.length <= MAX_STORAGE_KEY_LENGTH &&
                !storageKey.contains("..")

    private fun isValidFileName(fileName: String): Boolean =
        fileName.isNotBlank() &&
            fileName.length <= MAX_FILE_NAME_LENGTH &&
            !fileName.contains("/") &&
            !fileName.contains("\\")

    private fun isValidContentType(contentType: String): Boolean =
        contentType.lowercase() in ALLOWED_CONTENT_TYPES

    private fun isValidFileSize(fileSize: Long): Boolean =
        fileSize in 1..MAX_FILE_SIZE

    private fun createStorageKey(processId: Int, fileName: String): String {
        val sanitizedFileName = fileName
            .trim()
            .replace(Regex("[^A-Za-z0-9._-]"), "_")

        return "processes/$processId/${UUID.randomUUID()}-$sanitizedFileName"
    }
}
