package pt.isel.ipw.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import pt.isel.ipw.domain.User
import pt.isel.ipw.repository.jdbi.mappers.UserMapper

fun Jdbi.configureWithAppRequirements(): Jdbi {

    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerRowMapper(User::class.java, UserMapper())

    return this
}