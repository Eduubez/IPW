package pt.isel.ipw.http.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import pt.isel.ipw.http.filters.JwtAuthenticationFilter
import pt.isel.ipw.services.auth.JwtTokenService
import tools.jackson.databind.ObjectMapper

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
class SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): SecurityFilterChain {

        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/users/**").permitAll()
                    .anyRequest().authenticated()
            }

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    @Bean
    fun jwtAuthenticationFilter(
        jwtTokenService: JwtTokenService,
        objectMapper: ObjectMapper
    ): JwtAuthenticationFilter {
        return JwtAuthenticationFilter(jwtTokenService, objectMapper)
    }
}

