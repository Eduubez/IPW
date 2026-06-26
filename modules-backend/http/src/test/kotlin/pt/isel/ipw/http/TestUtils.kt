package pt.isel.ipw.http

import org.jdbi.v3.core.Jdbi
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import pt.isel.ipw.domain.DTO.input.CreateProcessRequest

object TestUtils {
    const val TRIATOR_ID = 1
    const val INVESTIGATOR_ID = 2
    const val SUPERVISOR_ID = 3
    const val MANAGER_ID = 4
    const val ADMIN_ID = 5

    fun setUpSecurityContext(userId: Int, role: String) {
        val auth = UsernamePasswordAuthenticationToken(
            userId,
            null,
            listOf(SimpleGrantedAuthority("ROLE_$role"))
        )
        SecurityContextHolder.getContext().authentication = auth
    }

    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    fun clear(jdbi:Jdbi)  {
        jdbi.useHandle<Exception> { handle ->
            handle.execute("call public.sample_data()")
        }
    }

    const val VALID_FUTURE_DATE = "2028-01-01T00:00:00"

    fun validCreateRequest() = CreateProcessRequest(
        name = "Valid Process Name",
        street = "Rua Augusta 1",
        county = "Lisbon",
        district = "Lisbon",
        area = "Car Accident",
        priority = "normal",
        expiresAt = VALID_FUTURE_DATE,
        investigatorId = INVESTIGATOR_ID,
        supervisorId = SUPERVISOR_ID,
        canBeFraud = false
    )



}