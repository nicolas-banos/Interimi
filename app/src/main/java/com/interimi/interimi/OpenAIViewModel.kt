package com.interimi.interimi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interimi.interimi.network.OpenAIRequest
import com.interimi.interimi.network.OpenAIResponse
import com.interimi.interimi.network.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class OpenAIViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _openAIResponse = MutableStateFlow<String>("")
    val openAIResponse: StateFlow<String> = _openAIResponse

    fun askOpenAI(question: String, userId: Int) {
        viewModelScope.launch {
            val user = userDao.getUserById(userId)
            val goals = user?.goals.orEmpty() // Obtiene metas del usuario
            val history = user?.history.orEmpty() // Obtiene historial actual
            val updatedHistory = history + "\n- $question" // Agrega la nueva consulta al historial

            // Actualiza el historial en la base de datos
            userDao.updateUserHistory(userId, updatedHistory)

            val formattedQuestion = "Responde de manera estoica a la siguiente consulta/problema: $question. Basándote en estas metas que tiene el usuario: $goals. Debes orientarlo a solucionar o responder su consulta, Sin dejar de lado sus metas conocidas. Estas son algunas consultas que este usuario te ha hecho en el pasado: ${history} Mencionalas únicamente si es necesario, pero utilizalas para guiarte."

            RetrofitInstance.api.getChatCompletion(OpenAIRequest(
                messages = listOf(mapOf("role" to "user", "content" to formattedQuestion))
            )).enqueue(object : Callback<OpenAIResponse> {
                override fun onResponse(call: Call<OpenAIResponse>, response: Response<OpenAIResponse>) {
                    if (response.isSuccessful) {
                        _openAIResponse.value = response.body()?.choices?.firstOrNull()?.message?.content ?: "Sin respuesta"
                    } else {
                        _openAIResponse.value = "Error: ${response.errorBody()?.string()}"
                    }
                }

                override fun onFailure(call: Call<OpenAIResponse>, t: Throwable) {
                    _openAIResponse.value = "Fallo en la conexión: ${t.message}"
                }
            })
        }
    }
}
