import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ipw.repository.jdbi.configureWithAppRequirements

object DbConfig {
    fun getConnection() = Jdbi.create(
        PGSimpleDataSource().apply {
            setUrl(Environment.getDbUrl())
        }
    ).configureWithAppRequirements()
}