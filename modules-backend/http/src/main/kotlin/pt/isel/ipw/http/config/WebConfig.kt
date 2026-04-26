package pt.isel.ipw.http.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.ipw.http.ApiRoutes
import pt.isel.ipw.http.interceptors.LoginTokenInterceptor
import pt.isel.ipw.http.interceptors.RefreshTokenInterceptor
import pt.isel.ipw.http.resolvers.LoginTokenPrincipalArgumentResolver
import pt.isel.ipw.http.resolvers.RefreshTokenPrincipalArgumentResolver

@Configuration
class WebConfig(
    private val loginTokenInterceptor: LoginTokenInterceptor,
    private val refreshTokenInterceptor: RefreshTokenInterceptor,
    private val loginTokenPrincipalArgumentResolver: LoginTokenPrincipalArgumentResolver,
    private val refreshTokenPrincipalArgumentResolver: RefreshTokenPrincipalArgumentResolver
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loginTokenInterceptor)
            .addPathPatterns(ApiRoutes.Users.SELECT_ROLE_FULL)

        registry.addInterceptor(refreshTokenInterceptor)
            .addPathPatterns(ApiRoutes.Users.REFRESH_TOKEN_FULL)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(loginTokenPrincipalArgumentResolver)
        resolvers.add(refreshTokenPrincipalArgumentResolver)
    }
}