package pt.isel.ipw.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import pt.isel.ipw.domain.AccessToken
import pt.isel.ipw.domain.RefreshToken
import pt.isel.ipw.domain.User
import pt.isel.ipw.repository.jdbi.mappers.AccessTokenMapper
import pt.isel.ipw.repository.jdbi.mappers.RefreshTokenMapper
import pt.isel.ipw.repository.jdbi.mappers.UserMapper

fun Jdbi.configureWithAppRequirements(): Jdbi {

    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerRowMapper(User::class.java, UserMapper())
    registerRowMapper(RefreshToken::class.java, RefreshTokenMapper())
    registerRowMapper(AccessToken::class.java, AccessTokenMapper())

    return this
}