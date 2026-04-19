package pt.isel.ipw.services.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pt.isel.ipw.domain.AccessToken
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import javax.crypto.SecretKey


@Service
class JwtTokenService(
    @param:Value("\${app.jwt.secret}") private val secret: String,
    @param:Value("\${app.jwt.access-token-ttl-minutes}") private val accessTokenTtlMinutes: Long,
    @param:Value("\${app.jwt.refresh-token-ttl-minutes}") private val refreshTokenTtlMinutes: Long,
) : TokenService {

    private val signingKey: SecretKey =
        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    override fun createAccessToken(
        userId: Int,
        roles: List<String>
    ): CreatedToken {
        val now = Instant.now()
        val expiresAt = now.plus(accessTokenTtlMinutes, ChronoUnit.MINUTES)

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

    override fun createRefreshToken(userId: Int): CreatedToken {
        val now = Instant.now()
        val expiresAt = now.plus(refreshTokenTtlMinutes, ChronoUnit.MINUTES)

        val token = Jwts.builder()
            .subject(userId.toString())
            .claim("userId", userId)
            .claim("type", "refresh")
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(signingKey)
            .compact()

        return CreatedToken(
            token = token,
            expiresAt = expiresAt
        )
    }

    override fun parseAccessToken(token: String): TokenClaims {
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

    override fun parseRefreshToken(token: String): Int {
        val claims = Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload

        val type = claims["type"] as? String
        if(type != "refresh") {
            throw IllegalArgumentException("Invalid refresh token type")
        }

        return (claims["userId"] as Number).toInt()
    }
    override fun isValid(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }
}