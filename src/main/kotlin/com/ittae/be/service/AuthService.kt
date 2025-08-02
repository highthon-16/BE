package com.ittae.be.service

import com.ittae.be.dto.LoginRequest
import com.ittae.be.dto.SignUpRequest
import com.ittae.be.dto.TokenResponse
import com.ittae.be.dto.UserResponse
import com.ittae.be.exception.DuplicateEmailException
import com.ittae.be.exception.UserNotFoundException
import com.ittae.be.model.AiStyle
import com.ittae.be.model.User
import com.ittae.be.repository.UserRepository
import com.ittae.be.security.JwtTokenProvider
import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    fun signUp(signUpRequest: SignUpRequest): UserResponse {
        logger.info("User sign up attempt: {}", signUpRequest.email)
        
        if (userRepository.findByEmail(signUpRequest.email) != null) {
            logger.warn("Sign up failed - email already exists: {}", signUpRequest.email)
            throw DuplicateEmailException("이미 사용 중인 이메일입니다: ${signUpRequest.email}")
        }
        
        try {
            val user = User(
                username = signUpRequest.username,
                email = signUpRequest.email,
                passwordHash = passwordEncoder.encode(signUpRequest.password)
            )
            val savedUser = userRepository.save(user)
            logger.info("User successfully signed up: {}", savedUser.email)
            return toResponse(savedUser)
        } catch (e: Exception) {
            logger.error("Failed to sign up user: {}", signUpRequest.email, e)
            throw e
        }
    }

    fun login(loginRequest: LoginRequest): TokenResponse {
        logger.info("Login attempt for email: {}", loginRequest.email)
        
        val user = userRepository.findByEmail(loginRequest.email)
        if (user == null) {
            logger.warn("Login failed - user not found: {}", loginRequest.email)
            throw UserNotFoundException("이메일 또는 비밀번호가 올바르지 않습니다.")
        }

        if (!passwordEncoder.matches(loginRequest.password, user.passwordHash)) {
            logger.warn("Login failed - invalid password for user: {}", loginRequest.email)
            throw IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.")
        }

        try {
            val userDetails = UserDetailsImpl(user)
            val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            SecurityContextHolder.getContext().authentication = authentication

            val accessToken = jwtTokenProvider.createToken(authentication)
            logger.info("User successfully logged in: {}", user.email)
            return TokenResponse(accessToken)
        } catch (e: Exception) {
            logger.error("Failed to generate token for user: {}", loginRequest.email, e)
            throw RuntimeException("로그인 처리 중 오류가 발생했습니다.")
        }
    }

    private fun toResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id,
            username = user.username,
            email = user.email,
            goals = user.goals,
            aiStyle = user.aiStyle ?: AiStyle.ADULT_WOMAN,
            currentStamina = user.currentStamina
        )
    }
}
