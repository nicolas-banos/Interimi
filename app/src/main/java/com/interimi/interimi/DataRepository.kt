package com.interimi.interimi.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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
    suspend fun deleteGoalsByUserId(userId: Int) = userDao.deleteGoalsByUserId(userId)
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
                val formattedQuestion = """
                Eres un mentor sabio, combinando la filosofía estoica con la visión de un padre. 
                No das discursos vacíos, sino enseñanzas basadas en la realidad. No todo se logra con esfuerzo, 
                pero quien apunta alto y avanza con disciplina, llega más lejos que quien ni siquiera lo intenta.

                📌 **Consulta del usuario:**  
                $question

                📌 **Metas del usuario:**  
                $goals

                📢 **Cómo debes responder:**
                - 📖 **Si es un PROBLEMA**, analiza la situación y da **varias soluciones posibles (mínimo 2-3), con ventajas y desventajas**.
                - 🎯 **Si es un CONSEJO**, ofrece **una única respuesta clara** si es evidente, o **varias perspectivas** si hay más de una forma de verlo.
                - 🏛️ **Usa la filosofía estoica + visión paterna:** Sé realista, directo y útil.
                - ❌ **No uses motivación barata.** No todo es posible, pero siempre hay un mejor camino.

                Ejemplo del tono correcto:
                ❌ "Si trabajas duro, todo es posible." → (Evita esto)  
                ✅ "Puedes esforzarte al máximo y aun así fracasar. Pero si no lo intentas, el fracaso es seguro. Lo importante no es ganar siempre, sino estar preparado cuando la oportunidad llegue."  

                Ahora, analiza y responde de la mejor manera posible pero breve, como si fuese una conversación real..
            """.trimIndent()

                Log.d("OPENAI PROMPT", formattedQuestion)

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

