package pt.isel.ipw.http

import jakarta.servlet.http.Cookie

object Cookies {
    const val AUTH_COOKIE = "auth_token"
    const val REFRESH_COOKIE = "refresh_token"

    const val COOKIE_HTTP_ONLY = true
    const val COOKIE_SECURE = false   // false agora pois em localhost o cliente nao pode enviar o cookie de volt

    const val AUTH_COOKIE_PATH = "/"
    const val REFRESH_COOKIE_PATH = "/users/refresh-token"

    const val AUTH_COOKIE_MAX_AGE = 120 * 60
    const val REFRESH_COOKIE_MAX_AGE = 7 * 24 * 60 * 60

    fun createAuthCookie(token: String) = Cookie(AUTH_COOKIE, token).apply {
        isHttpOnly = COOKIE_HTTP_ONLY
        secure = COOKIE_SECURE
        path = AUTH_COOKIE_PATH
        maxAge = AUTH_COOKIE_MAX_AGE
    }

    fun createRefreshCookie(token: String) = Cookie(REFRESH_COOKIE, token).apply {
        isHttpOnly = COOKIE_HTTP_ONLY
        secure = COOKIE_SECURE
        path = REFRESH_COOKIE_PATH
        maxAge = REFRESH_COOKIE_MAX_AGE
    }
}