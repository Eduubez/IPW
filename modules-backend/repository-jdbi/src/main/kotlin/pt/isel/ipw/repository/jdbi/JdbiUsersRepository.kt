package pt.isel.ipw.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.ipw.domain.User
import pt.isel.ipw.repository.UsersRepository
import pt.isel.ipw.repository.jdbi.mappers.UserMapper

class JdbiUsersRepository(
    private val handle: Handle
) : UsersRepository {

    override fun getUserByEmail(email: String): User? {
        return handle.createQuery(
            """
                select 
                u.id,
                u.name,
                u.email,
                u.password_hash,
                a.name as area,
                u.is_active
                from Users u
                left join Area a on u.area_id = a.id
                where u.email = :email
            """
        )
            .bind("email", email)
            .map(UserMapper())
            .singleOrNull()
    }

    override fun getUserRoles(userId: Int): List<String> {
        return handle.createQuery(
            """
                select role_name
                from User_Role
                where user_id = :userId
            """
        )
            .bind("userId", userId)
            .mapTo(String::class.java)
            .list()
    }

    override fun isUserStoredByEmail(email: String): Boolean {
        return handle.createQuery(
            """
                select count(*) 
                from Users
                where email = :email
            """
        )
            .bind("email", email)
            .mapTo(Int::class.java)
            .one() > 0
    }

    override fun isUserStoredById(userId: Int): Boolean {
        return handle.createQuery(
            """
            select count(*)
            from Users
            where id = :id
        """
        )
            .bind("id", userId)
            .mapTo<Int>()
            .one() > 0
    }

    override fun createUser(
        name: String,
        email: String,
        passwordHash: String,
        areaId: Int?
    ): Int {
        return handle.createUpdate(
            """
                insert into Users(name, email, password_hash, area_id)
                values (:name, :email, :passwordHash, :areaId)
            """
        )
            .bind("name", name)
            .bind("email", email)
            .bind("passwordHash", passwordHash)
            .bind("areaId", areaId)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()
    }

    override fun addUserRole(userId: Int, roleName: String) {
        handle.createUpdate(
            """
                insert into User_Role(user_id, role_name)
                values (:userId, :roleName)
            """
        )
            .bind("userId", userId)
            .bind("roleName", roleName)
            .execute()
    }

    override fun getUserById(userId: Int): User? {
        return handle.createQuery(
            """
                select name,email, area_id,password_hash, is_active
                from Users                
                where id = :userId
            """
        )
            .bind("userId", userId)
            .map(UserMapper())
            .singleOrNull()
    }
}