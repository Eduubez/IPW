package pt.isel.ipw.http.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import pt.isel.ipw.http.Cookies
import pt.isel.ipw.services.auth.JwtTokenService

class JwtAuthenticationFilter(
    private val jwtTokenService: JwtTokenService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = extractToken(request)

        if (token != null && jwtTokenService.isValid(token)) {
            val tokenClaims = jwtTokenService.parseAccessToken(token)

            val authorities = tokenClaims.roles.map {
                SimpleGrantedAuthority("ROLE_${it.uppercase()}")
            }
            val auth = UsernamePasswordAuthenticationToken(
                tokenClaims.userId,
                null,
                authorities
            )

            SecurityContextHolder.getContext().authentication = auth
        }
        filterChain.doFilter(request, response)
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7)
        }
        return request.cookies
            ?.firstOrNull { it.name == Cookies.AUTH_COOKIE }
            ?.value
    }
}