package pt.isel.ipw.http.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import pt.isel.ipw.http.ApiRoutes
import pt.isel.ipw.http.TokenExtractor.extractToken
import pt.isel.ipw.http.errors.Problem
import pt.isel.ipw.http.errors.toHttp
import pt.isel.ipw.services.auth.ExpiredAccessTokenException
import pt.isel.ipw.services.auth.InvalidTokenException
import pt.isel.ipw.services.auth.JwtTokenService
import pt.isel.ipw.services.errors.UserError
import tools.jackson.databind.ObjectMapper


class JwtAuthenticationFilter(
    private val jwtTokenService: JwtTokenService,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath

        return path in setOf(
            ApiRoutes.Users.LOGIN_FULL,
            ApiRoutes.Users.SELECT_ROLE_FULL,
            ApiRoutes.Users.REFRESH_TOKEN_FULL,
            ApiRoutes.Users.ROLES_FULL
        )
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

        val tokenClaims = try {
            jwtTokenService.parseAccessToken(token)
        } catch (_: ExpiredAccessTokenException) {
            writeProblem(response, UserError.ExpiredAccessToken)
            return
        } catch (_: InvalidTokenException) {
            writeProblem(response, UserError.InvalidToken)
            return
        }

        val authorities = listOf(
            SimpleGrantedAuthority("ROLE_${tokenClaims.role}")
        )

        val auth = UsernamePasswordAuthenticationToken(
            tokenClaims.userId,
            null,
            authorities
        )

        SecurityContextHolder.getContext().authentication = auth
        filterChain.doFilter(request, response)
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
