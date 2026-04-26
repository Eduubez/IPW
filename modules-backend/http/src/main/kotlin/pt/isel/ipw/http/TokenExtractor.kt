package pt.isel.ipw.http

import jakarta.servlet.http.HttpServletRequest

object TokenExtractor {

    fun extractToken(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7)
        }

        return request.cookies
            ?.firstOrNull { it.name == Cookies.AUTH_COOKIE }
            ?.value
    }
}