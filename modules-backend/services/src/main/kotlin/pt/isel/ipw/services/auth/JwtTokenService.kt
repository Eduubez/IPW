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
    @param:Value("\${app.jwt.login-token-ttl-minutes}") private val loginTokenTtlMinutes: Long,
    @param:Value("\${app.jwt.access-token-ttl-minutes}") private val accessTokenTtlMinutes: Long,
    @param:Value("\${app.jwt.refresh-token-ttl-minutes}") private val refreshTokenTtlMinutes: Long,
) : TokenService {

    private val signingKey: SecretKey =
        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))


    override fun createLoginToken(userId: Int): CreatedToken {
        val now = Instant.now()
        val expiresAt = now.plus(loginTokenTtlMinutes, ChronoUnit.MINUTES)

        val token = Jwts.builder()
            .subject(userId.toString())
            .claim("userId", userId)
            .claim("type", "login")
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(signingKey)
            .compact()

        return CreatedToken(
            token = token,
            expiresAt = expiresAt
        )
    }

    override fun createAccessToken(userId: Int, role: String): CreatedToken {
        val now = Instant.now()
        val expiresAt = now.plus(accessTokenTtlMinutes, ChronoUnit.MINUTES)

        val token = Jwts.builder()
            .subject(userId.toString())
            .claim("userId", userId)
            .claim("role", role)
            .claim("type", "access")
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(signingKey)
            .compact()

        return CreatedToken(
            token = token,
            expiresAt = expiresAt
        )
    }

    override fun createRefreshToken(userId: Int, role: String): CreatedToken {
        val now = Instant.now()
        val expiresAt = now.plus(refreshTokenTtlMinutes, ChronoUnit.MINUTES)

        val token = Jwts.builder()
            .subject(userId.toString())
            .claim("userId", userId)
            .claim("role", role)
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

    override fun parseLoginToken(token: String): Int {
        val claims = getClaims(token)

        val type = claims["type"] as? String
        if (type != "login") {
            throw IllegalArgumentException("Invalid login token type")
        }

        return (claims["userId"] as Number).toInt()
    }

    override fun parseAccessToken(token: String): TokenClaims {
        val claims = getClaims(token)

        val type = claims["type"] as? String
        if (type != "access") {
            throw IllegalArgumentException("Invalid access token type")
        }

        val userId = (claims["userId"] as Number).toInt()
        val role = claims["role"] as String

        return TokenClaims(
            userId = userId,
            role = role
        )
    }

    override fun parseRefreshToken(token: String): TokenClaims {
        val claims = getClaims(token)

        val type = claims["type"] as? String
        if (type != "refresh") {
            throw IllegalArgumentException("Invalid refresh token type")
        }

        val userId = (claims["userId"] as Number).toInt()
        val role = claims["role"] as String

        return TokenClaims(
            userId = userId,
            role = role
        )
    }

    private fun getClaims(token: String) =
        Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload

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