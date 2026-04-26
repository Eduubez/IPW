package pt.isel.ipw.services.auth

import io.jsonwebtoken.ExpiredJwtException
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


    override fun createLoginToken(userId: Int, roles: List<String>): CreatedToken {
        val now = Instant.now()
        val expiresAt = now.plus(loginTokenTtlMinutes, ChronoUnit.MINUTES)

        val token = Jwts.builder()
            .subject(userId.toString())
            .claim("userId", userId)
            .claim("roles", roles)
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

    override fun parseLoginToken(token: String): LoginTokenClaims {
        val claims = getClaimsOrThrow(token, ExpiredLoginTokenException())

        val type = claims["type"] as? String
            ?: throw InvalidTokenException()

        if (type != "login") {
            throw InvalidTokenException()
        }

        val userId = (claims["userId"] as? Number)?.toInt()
            ?: throw InvalidTokenException()

        val roles = (claims["roles"] as? List<*>)?.filterIsInstance<String>()
            ?: throw InvalidTokenException()

        return LoginTokenClaims(
            userId = userId,
            roles = roles
        )
    }

    override fun parseAccessToken(token: String): TokenClaims {
        val claims = getClaimsOrThrow(token, ExpiredAccessTokenException())

        val type = claims["type"] as? String
            ?: throw InvalidTokenException()

        if (type != "access") {
            throw InvalidTokenException()
        }

        val userId = (claims["userId"] as? Number)?.toInt()
            ?: throw InvalidTokenException()

        val role = claims["role"] as? String
            ?: throw InvalidTokenException()

        return TokenClaims(
            userId = userId,
            role = role
        )
    }

    override fun parseRefreshToken(token: String): TokenClaims {
        val claims = getClaimsOrThrow(token, ExpiredRefreshTokenException())

        val type = claims["type"] as? String
            ?: throw InvalidTokenException()

        if (type != "refresh") {
            throw InvalidTokenException()
        }

        val userId = (claims["userId"] as? Number)?.toInt()
            ?: throw InvalidTokenException()

        val role = claims["role"] as? String
            ?: throw InvalidTokenException()

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

    private fun getClaimsOrThrow(
        token: String,
        expiredException: RuntimeException
    ) = try {
        getClaims(token)
    } catch (_: ExpiredJwtException) {
        throw expiredException
    } catch (_: Exception) {
        throw InvalidTokenException()
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