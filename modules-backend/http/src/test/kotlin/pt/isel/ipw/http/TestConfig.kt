package pt.isel.ipw.http

import org.jdbi.v3.core.Jdbi
import org.springframework.context.annotation.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import pt.isel.ipw.repository.jdbi.config.JdbiConfig
import pt.isel.ipw.repository.jdbi.transaction.JdbiTransactionManager
import pt.isel.ipw.services.auth.JwtTokenService
import pt.isel.ipw.services.interfaces.ProveStorageService
import tools.jackson.databind.ObjectMapper

@Configuration
@ComponentScan(
    "pt.isel",
    excludeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [JdbiConfig::class])]
)class TestConfig {
    @Bean
    fun jdbi(): Jdbi = DbConfig.getConnection()


    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper()

    @Bean
    fun jwtTokenService(): JwtTokenService = JwtTokenService(
        secret = "1234567890123456789012345678901234567890123456789012345678901234",
        loginTokenTtlMinutes = 5,
        accessTokenTtlMinutes = 120,
        refreshTokenTtlMinutes = 10080
    )

    @Bean
    @Primary
    fun trxManager() = JdbiTransactionManager(jdbi())

    @Bean
    @Primary
    fun proveStorageService(): ProveStorageService =
        object : ProveStorageService {
            override fun createUploadUrl(storageKey: String, fileName: String): String =
                "http://storage.test/upload/$storageKey"

            override fun createAccessUrl(storageKey: String, fileName: String): String =
                "http://storage.test/access/$storageKey"

            override fun deleteObject(storageKey: String) = Unit

            override fun objectExists(storageKey: String): Boolean =
                !storageKey.contains("missing")
        }

}
