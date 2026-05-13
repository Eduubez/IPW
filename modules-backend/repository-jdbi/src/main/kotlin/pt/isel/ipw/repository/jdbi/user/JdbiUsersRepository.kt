package pt.isel.ipw.repository.jdbi.user

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.ipw.domain.DTO.output.user.AssignableUser
import pt.isel.ipw.domain.user.User
import pt.isel.ipw.domain.user.UserWithRoles
import pt.isel.ipw.repository.UsersRepository

class JdbiUsersRepository(
    private val handle: Handle
) : UsersRepository {

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
            .mapTo<User>()
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

    override fun addUserRoles(userId: Int, roles: List<String>) {
        val batch = handle.prepareBatch(
            "insert into User_Role(user_id, role_name) values (:userId, :role)"
        )

        roles.forEach { role ->
            batch.bind("userId", userId)
                .bind("role", role)
                .add()
        }

        batch.execute()
    }

    override fun getUserById(userId: Int): User? {
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
            where u.id = :userId
        """
        )
            .bind("userId", userId)
            .mapTo<User>()
            .singleOrNull()
    }

    override fun getUserWithRolesById(userId: Int): UserWithRoles? {
        return handle.createQuery(
            """
            select
                u.id,
                u.name,
                u.email,
                u.area_id,
                a.name as area,
                u.is_active,
                coalesce(
                    array_agg(ur.role_name) filter (where ur.role_name is not null),
                    '{}'
                ) as roles
            from Users u
            left join Area a on u.area_id = a.id
            left join User_Role ur on ur.user_id = u.id
            where u.id = :userId
            group by u.id, u.name, u.email, u.area_id, a.name, u.is_active
        """
        )
            .bind("userId", userId)
            .mapTo<UserWithRoles>()
            .singleOrNull()
    }

    override fun getAllUsers(offset: Int, limit: Int): List<UserWithRoles> {
        return handle.createQuery(
            """
            select
                u.id,
                u.name,
                u.email,
                u.area_id,
                a.name as area,
                u.is_active,
                coalesce(
                    array_agg(ur.role_name) filter (where ur.role_name is not null),
                    '{}'
                ) as roles
            from Users u
            left join Area a on u.area_id = a.id
            left join User_Role ur on ur.user_id = u.id
            group by u.id, u.name, u.email, u.area_id, a.name, u.is_active
            order by u.id
            offset :offset
            limit :limit
        """
        )
            .bind("offset", offset)
            .bind("limit", limit)
            .mapTo<UserWithRoles>()
            .list()
    }

    override fun getAssignableUsersByRole(
        role: String,
        areaId: Int?
    ): List<AssignableUser> {
        val query = """
        select
            u.id,
            u.name,
            a.id as area_id,
            a.name as area
        from Users u
        join User_Role ur on ur.user_id = u.id
        join Area a on a.id = u.area_id
        where ur.role_name = :role
          and u.is_active = true
          and (:areaId::int is null or a.id = :areaId)
        order by u.name
    """.trimIndent()

        return handle.createQuery(query)
            .bind("role", role)
            .bind("areaId", areaId)
            .mapTo<AssignableUser>()
            .list()
    }

    override fun replaceUserRoles(userId: Int, roles: List<String>) {
        handle.createUpdate(
            """
            delete from User_Role
            where user_id = :userId
        """
        )
            .bind("userId", userId)
            .execute()

        addUserRoles(userId, roles)
    }

    override fun updateUserPassword(userId: Int, newPasswordHash: String) {
        handle.createUpdate(
            """
            update Users
            set password_hash = :newPasswordHash
            where id = :userId
        """
        )
            .bind("userId", userId)
            .bind("newPasswordHash", newPasswordHash)
            .execute()
    }

    override fun updateUserArea(userId: Int, areaId: Int?) {
        handle.createUpdate(
            """
            update Users
            set area_id = :areaId
            where id = :userId
        """
        )
            .bind("userId", userId)
            .bind("areaId", areaId)
            .execute()
    }

    override fun removeUserRole(userId: Int, role: String) {
        handle.createUpdate(
            """
            delete from User_Role
            where user_id = :userId
              and role_name = :role
        """
        )
            .bind("userId", userId)
            .bind("role", role)
            .execute()
    }
}