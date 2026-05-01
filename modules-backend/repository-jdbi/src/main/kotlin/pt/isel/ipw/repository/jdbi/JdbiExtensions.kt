package pt.isel.ipw.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import pt.isel.ipw.domain.AccessToken
import pt.isel.ipw.domain.Activity
import pt.isel.ipw.domain.Entities.area.AreaView
import pt.isel.ipw.domain.LoginToken
import pt.isel.ipw.domain.RefreshToken
import pt.isel.ipw.domain.user.User
import pt.isel.ipw.domain.user.UserWithRoles
import pt.isel.ipw.repository.jdbi.mappers.AccessTokenMapper
import pt.isel.ipw.repository.jdbi.mappers.ActivityMapper
import pt.isel.ipw.repository.jdbi.mappers.AreaViewMapper
import pt.isel.ipw.repository.jdbi.mappers.LoginTokenMapper
import pt.isel.ipw.repository.jdbi.mappers.RefreshTokenMapper
import pt.isel.ipw.repository.jdbi.mappers.UserMapper
import pt.isel.ipw.repository.jdbi.mappers.UserWithRolesMapper

fun Jdbi.configureWithAppRequirements(): Jdbi {

    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerRowMapper(User::class.java, UserMapper())
    registerRowMapper(UserWithRoles::class.java, UserWithRolesMapper())
    registerRowMapper(RefreshToken::class.java, RefreshTokenMapper())
    registerRowMapper(AccessToken::class.java, AccessTokenMapper())
    registerRowMapper(LoginToken::class.java, LoginTokenMapper())
    registerRowMapper(Activity::class.java, ActivityMapper())
    registerRowMapper(AreaView::class.java, AreaViewMapper())

    return this
}