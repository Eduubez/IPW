package pt.isel.ipw.services.interfaces

interface ProveStorageService {
    fun createUploadUrl(storageKey: String): String

    fun createAccessUrl(storageKey: String): String

    fun deleteObject(storageKey: String)

    fun objectExists(storageKey: String): Boolean
}