package pt.isel.ipw.http.filters

import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import pt.isel.ipw.http.Cookies
import pt.isel.ipw.http.errors.Problem
import pt.isel.ipw.http.errors.toHttp
import pt.isel.ipw.services.auth.JwtTokenService
import pt.isel.ipw.services.errors.UserError
import tools.jackson.databind.ObjectMapper

class JwtAuthenticationFilter(
    private val jwtTokenService: JwtTokenService,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path == "/users" ||
                path == "/users/login" ||
                path == "/users/auth/select-role" ||
                path == "/users/refresh-token" ||
                path == "/users/roles"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = extractToken(request)

        if (token == null) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val tokenClaims = jwtTokenService.parseAccessToken(token)

            val authorities = listOf(
                SimpleGrantedAuthority("ROLE_${tokenClaims.role.uppercase()}")
            )

            val auth = UsernamePasswordAuthenticationToken(
                tokenClaims.userId,
                null,
                authorities
            )

            SecurityContextHolder.getContext().authentication = auth
            filterChain.doFilter(request, response)

        } catch (_: ExpiredJwtException) {
            writeProblem(response, UserError.ExpiredAccessToken)

        } catch (_: Exception) {
            writeProblem(response, UserError.InvalidToken)
        }
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

    private fun writeProblem(
        response: HttpServletResponse,
        error: UserError
    ) {
        val (status, problem) = error.toHttp()
        response.status = status
        response.contentType = Problem.MEDIA_TYPE
        response.characterEncoding = Charsets.UTF_8.name()
        response.writer.write(objectMapper.writeValueAsString(problem))
    }
}