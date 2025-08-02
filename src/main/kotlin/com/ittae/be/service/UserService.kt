package com.ittae.be.service

import com.ittae.be.dto.SetAiStyleRequest
import com.ittae.be.dto.SetGoalRequest
import com.ittae.be.dto.UserResponse
import com.ittae.be.exception.UserNotFoundException
import com.ittae.be.model.AiStyle
import com.ittae.be.model.User
import com.ittae.be.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun setGoal(setGoalRequest: SetGoalRequest, userId: Long): UserResponse {
        logger.info("Setting goal for user: {}, goal: {}", userId, setGoalRequest.goals)
        try {
            val user = getUserById(userId)
            user.goals = setGoalRequest.goals
            val savedUser = userRepository.save(user)
            logger.info("Successfully set goal for user: {}", userId)
            return toResponse(savedUser)
        } catch (e: Exception) {
            logger.error("Failed to set goal for user: {}", userId, e)
            throw e
        }
    }

    fun setAiStyle(setAiStyleRequest: SetAiStyleRequest, userId: Long): UserResponse {
        logger.info("Setting AI style for user: {}, style: {}", userId, setAiStyleRequest.aiStyle)
        try {
            val user = getUserById(userId)
            user.aiStyle = setAiStyleRequest.aiStyle
            val savedUser = userRepository.save(user)
            logger.info("Successfully set AI style for user: {}", userId)
            return toResponse(savedUser)
        } catch (e: Exception) {
            logger.error("Failed to set AI style for user: {}", userId, e)
            throw e
        }
    }

    private fun getUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { 
                logger.warn("User not found: {}", userId)
                UserNotFoundException("사용자를 찾을 수 없습니다. ID: $userId") 
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
