package pt.isel.ipw.services.interfaces

interface ProveStorageService {
    fun createUploadUrl(storageKey: String,fileName:String): String

    fun createAccessUrl(storageKey: String,fileName: String): String

    fun deleteObject(storageKey: String)

    fun objectExists(storageKey: String): Boolean
}