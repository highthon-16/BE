package com.ittae.be.security

import com.ittae.be.service.UserDetailsImpl
import com.ittae.be.service.UserDetailsServiceImpl
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey
import org.springframework.core.env.Environment
import org.springframework.beans.factory.annotation.Autowired

@Component
class JwtTokenProvider @Autowired constructor(
    private val userDetailsService: UserDetailsServiceImpl,
    private val environment: Environment  // Environment 주입
) {
    // application.properties에서 값 읽기 (이 방식은 오류 안 나며, Kotlin에서도 동작)
    val secret: String = environment.getProperty("jwt.secret")
        ?: throw IllegalStateException("jwt.secret property is not set!")
    val expiration: Long = environment.getProperty("jwt.expiration", Long::class.java)
        ?: throw IllegalStateException("jwt.expiration property is not set!")

    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun createToken(authentication: Authentication): String {
        val userDetails = authentication.principal as UserDetailsImpl
        val now = Date()
        val validity = Date(now.time + expiration)

        return Jwts.builder()
            .setSubject(userDetails.username)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val userDetails = userDetailsService.loadUserByUsername(getUsername(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun getUsername(token: String): String {
        return getClaims(token).subject
    }

    fun validateToken(token: String): Boolean {
        return try {
            !getClaims(token).expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload()
    }
}
