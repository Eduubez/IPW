package pt.isel.ipw.http.interceptors

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.ipw.http.TokenExtractor.extractToken
import pt.isel.ipw.http.auth.AuthRequestAttributes
import pt.isel.ipw.http.auth.RefreshTokenPrincipal
import pt.isel.ipw.services.auth.InvalidTokenException
import pt.isel.ipw.services.auth.JwtTokenService

@Component
class RefreshTokenInterceptor(
    private val jwtTokenService: JwtTokenService
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val token = extractToken(request)
            ?: throw InvalidTokenException()

        val claims = jwtTokenService.parseRefreshToken(token)

        request.setAttribute(
            AuthRequestAttributes.REFRESH_TOKEN_PRINCIPAL,
            RefreshTokenPrincipal(token, claims)
        )

        return true
    }
}