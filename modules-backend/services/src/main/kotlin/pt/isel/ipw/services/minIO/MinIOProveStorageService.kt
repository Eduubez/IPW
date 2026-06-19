package pt.isel.ipw.services.minIO

import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.RemoveObjectArgs
import io.minio.StatObjectArgs
import io.minio.http.Method
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pt.isel.ipw.services.interfaces.ProveStorageService
import java.util.concurrent.TimeUnit

@Service
class MinIOProveStorageService(
    @param:Value("\${minio.internal-endpoint}") private val internalEndpoint: String,
    @param:Value("\${minio.public-endpoint}") private val publicEndpoint: String,
    @param:Value("\${minio.access-key}") private val accessKey: String,
    @param:Value("\${minio.secret-key}") private val secretKey: String,
    @param:Value("\${minio.bucket}") private val bucket: String
) : ProveStorageService {

    companion object {
        // O MinIO corre localmente, mas os presigned URLs usam o processo de assinatura S3,
        // que exige uma regiao. Fixar este valor evita que o SDK tente descobri-la pelo endpoint publico.
        private const val S3_SIGNING_REGION = "us-east-1"
    }

    private val internalClient: MinioClient by lazy {
        MinioClient.builder()
            .endpoint(internalEndpoint)
            .credentials(accessKey, secretKey)
            .region(S3_SIGNING_REGION)
            .build()
    }

    private val publicClient: MinioClient by lazy {
        MinioClient.builder()
            .endpoint(publicEndpoint)
            .credentials(accessKey, secretKey)
            .region(S3_SIGNING_REGION)
            .build()
    }

    override fun createUploadUrl(
        storageKey: String,
        fileName: String,
    ): String = createUrl(Method.PUT, storageKey,fileName)

    override fun createAccessUrl(
        storageKey: String,
        fileName: String
    ): String = createUrl(Method.GET, storageKey,fileName)

    override fun deleteObject(
        storageKey: String
    ) {
        internalClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucket)
                .`object`(storageKey)
                .build()
        )
    }

    override fun objectExists(
        storageKey: String
    ): Boolean =
        try {
            internalClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucket)
                    .`object`(storageKey)
                    .build()
            )
            true
        } catch (_: Exception) {
            false
        }

    private fun createUrl(
        method: Method,
        storageKey: String,
        fileName: String
    ): String =
        publicClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(method)
                .bucket(bucket)
                .`object`(storageKey)
                .expiry(10, TimeUnit.MINUTES)
                .extraQueryParams(
                    mapOf(
                        "response-content-disposition" to
                                "attachment; filename=\"$fileName\""
                    )
                )
                .build()
        )
}
