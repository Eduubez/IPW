package pt.isel.ipw.http.resolvers

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import pt.isel.ipw.http.auth.AuthRequestAttributes
import pt.isel.ipw.http.auth.AuthenticatedLogin
import pt.isel.ipw.http.auth.LoginTokenPrincipal
import pt.isel.ipw.services.auth.InvalidTokenException
@Component
class LoginTokenPrincipalArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AuthenticatedLogin::class.java) &&
                parameter.parameterType == LoginTokenPrincipal::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val request = webRequest.nativeRequest as HttpServletRequest

        return request.getAttribute(AuthRequestAttributes.LOGIN_TOKEN_PRINCIPAL)
            ?: throw InvalidTokenException()
    }
}