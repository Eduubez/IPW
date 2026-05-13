package pt.isel.ipw.http.auth

import org.springframework.security.core.context.SecurityContextHolder

object AuthenticatedUser {
    fun id(): Int? =
        SecurityContextHolder
            .getContext()
            .authentication
            ?.principal as? Int

    fun role(): String? =
        SecurityContextHolder
            .getContext()
            .authentication
            ?.authorities
            ?.firstOrNull()
            ?.authority
            ?.removePrefix("ROLE_")
            ?.lowercase()
}
