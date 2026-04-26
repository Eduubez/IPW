package pt.isel.ipw.http

import jakarta.servlet.http.HttpServletRequest

object TokenExtractor {

    fun extractToken(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization") ?: return null
        if (!header.startsWith("Bearer ")) return null
        return header.removePrefix("Bearer ").trim()
    }
}
