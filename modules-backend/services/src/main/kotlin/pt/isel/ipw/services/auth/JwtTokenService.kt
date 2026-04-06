package pt.isel.ipw.services.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import javax.crypto.SecretKey


@Service
class JwtTokenService(
    @param:Value("\${app.jwt.secret}") private val secret: String,
    @param:Value("\${app.jwt.ttl-minutes}") private val ttlMinutes: Long,
) : TokenService {

    private val signingKey: SecretKey =
        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    override fun createLoginToken(
        userId: Int,
        roles: List<String>
    ): CreatedToken {
        val now = Instant.now()
        val expiresAt = now.plus(ttlMinutes, ChronoUnit.MINUTES)

        val token = Jwts.builder()
            .subject(userId.toString())
            .claim("userId", userId)
            .claim("roles", roles)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(signingKey)
            .compact()

        return CreatedToken(
            token = token,
            expiresAt = expiresAt
        )
    }

    override fun parseToken(token: String): TokenClaims {
        val claims = Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload

        val userId = (claims["userId"] as Number).toInt()
        val roles = claims["roles"] as List<*>

        return TokenClaims(
            userId = userId,
            roles = roles.map { it as String }
        )
    }

}