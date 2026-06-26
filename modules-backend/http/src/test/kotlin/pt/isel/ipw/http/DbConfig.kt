package pt.isel.ipw.http

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ipw.repository.jdbi.configureWithAppRequirements


object DbConfig {
    fun getConnection() = Jdbi.create(
        PGSimpleDataSource().apply {
            setUrl("jdbc:postgresql://localhost:5434/ipw_test")
            user = "postgres"
            password = "1234"
        }
    ).configureWithAppRequirements()
}