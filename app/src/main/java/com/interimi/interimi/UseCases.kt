package com.interimi.interimi.domain.usecases

import com.interimi.interimi.User
import com.interimi.interimi.data.OpenAIRepository
import com.interimi.interimi.data.PreferencesRepository
import com.interimi.interimi.data.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UseCases @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository,
    private val openAIRepository: OpenAIRepository
) {
    suspend fun insertUser(user: User): Long = userRepository.insertUser(user)
    suspend fun getUserById(userId: Int): User? = userRepository.getUserById(userId)
    suspend fun deleteGoalsByUserId(userId: Int)= userRepository.deleteGoalsByUserId(userId)
    suspend fun updateUserGoals(userId: Int, newGoal: String) {
        val user = getUserById(userId)
        val updatedGoals = (user?.goals ?: "").plus("\n- $newGoal")
        userRepository.updateUserGoals(userId, updatedGoals.trim())
    }
    suspend fun saveUserHistory(history: String) = preferencesRepository.saveUserHistory(history)
    fun getUserHistory(): Flow<String> = preferencesRepository.getUserHistory()
    suspend fun askOpenAI(question: String, userId: Int): String = openAIRepository.askOpenAI(question, userId)
}

