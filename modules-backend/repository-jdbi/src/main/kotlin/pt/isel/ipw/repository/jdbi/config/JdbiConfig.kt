package pt.isel.ipw.repository.jdbi.config

import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ipw.repository.jdbi.configureWithAppRequirements


@Configuration
class JdbiConfig {

    @Bean
    fun jdbi(
        @Value("\${spring.datasource.url}") dbUrl: String,
        @Value("\${spring.datasource.username}") dbUser: String,
        @Value("\${spring.datasource.password}") dbPassword: String,
    ): Jdbi {
        val dataSource = PGSimpleDataSource()
        dataSource.setURL(dbUrl)
        dataSource.user = dbUser
        dataSource.password = dbPassword

        return Jdbi
            .create(dataSource)
            .configureWithAppRequirements()
    }
}