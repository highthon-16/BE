package com.ittae.be.service

import com.ittae.be.dto.LoginRequest
import com.ittae.be.dto.SignUpRequest
import com.ittae.be.dto.TokenResponse
import com.ittae.be.model.User
import com.ittae.be.repository.UserRepository
import com.ittae.be.security.JwtTokenProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    fun signUp(signUpRequest: SignUpRequest): User {
        if (userRepository.findByEmail(signUpRequest.email) != null) {
            throw IllegalArgumentException("Email is already in use")
        }
        val user = User(
            username = signUpRequest.username,
            email = signUpRequest.email,
            passwordHash = passwordEncoder.encode(signUpRequest.password)
        )
        return userRepository.save(user)
    }

    fun login(loginRequest: LoginRequest): TokenResponse {
        val user = userRepository.findByEmail(loginRequest.email)
            ?: throw IllegalArgumentException("Invalid email or password")

        if (!passwordEncoder.matches(loginRequest.password, user.passwordHash)) {
            throw IllegalArgumentException("Invalid email or password")
        }

        val userDetails = UserDetailsImpl(user)
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authentication

        val accessToken = jwtTokenProvider.createToken(authentication)
        return TokenResponse(accessToken)
    }
}
