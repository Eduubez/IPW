package pt.isel.ipw.http

import org.jdbi.v3.core.Jdbi
import org.mockito.Mockito.mock
import org.springframework.context.annotation.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import pt.isel.ipw.repository.jdbi.config.JdbiConfig
import pt.isel.ipw.repository.jdbi.transaction.JdbiTransactionManager
import pt.isel.ipw.services.auth.JwtTokenService
import pt.isel.ipw.services.interfaces.NoteService
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
    fun jwtTokenService(): JwtTokenService = mock(JwtTokenService::class.java)

    @Bean
    @Primary
    fun trxManager() = JdbiTransactionManager(jdbi())

}
