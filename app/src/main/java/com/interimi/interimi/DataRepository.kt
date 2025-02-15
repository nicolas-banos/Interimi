package com.interimi.interimi.data

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import androidx.room.Room
import com.interimi.interimi.User
import com.interimi.interimi.UserDao
import com.interimi.interimi.network.OpenAIApiService
import com.interimi.interimi.network.OpenAIRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

// Repositorio de Base de Datos
@Singleton
class UserRepository @Inject constructor(private val userDao: UserDao) {
    suspend fun insertUser(user: User): Long = userDao.insertUser(user)
    suspend fun getUserById(userId: Int): User? = userDao.getUserById(userId)
    suspend fun updateUserHistory(userId: Int, history: String) = userDao.updateUserHistory(userId, history)
    suspend fun updateUserGoals(userId: Int, goals: String) = userDao.updateUserGoals(userId, goals)
}

// Repositorio de SharedPreferences / DataStore
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    private val USER_HISTORY_KEY = stringPreferencesKey("user_history")

    suspend fun saveUserHistory(history: String) {
        dataStore.edit { prefs ->
            prefs[USER_HISTORY_KEY] = history
        }
    }

    fun getUserHistory(): Flow<String> {
        return dataStore.data.map { prefs ->
            prefs[USER_HISTORY_KEY] ?: ""
        }
    }
}

// Repositorio de API (OpenAI)
@Singleton
class OpenAIRepository @Inject constructor(
    private val apiService: OpenAIApiService,
    private val userRepository: UserRepository
) {
    suspend fun askOpenAI(question: String, userId: Int): String {
        return withContext(Dispatchers.IO) {  // 👈 Ejecutar en un hilo de fondo
            try {
                val user = userRepository.getUserById(userId)
                val goals = user?.goals.orEmpty()
                val history = user?.history.orEmpty()
                val formattedQuestion = "Responde de manera estoica: $question. Metas: $goals. Historial: $history"

                val response = apiService.getChatCompletion(
                    OpenAIRequest(messages = listOf(mapOf("role" to "user", "content" to formattedQuestion)))
                ).execute()

                if (response.isSuccessful) {
                    response.body()?.choices?.firstOrNull()?.message?.content ?: "Sin respuesta"
                } else {
                    "Error en API: ${response.errorBody()?.string() ?: "Respuesta inválida"}"
                }
            } catch (e: Exception) {
                "Error en la solicitud: ${e.localizedMessage}"
            }
        }
    }
}

