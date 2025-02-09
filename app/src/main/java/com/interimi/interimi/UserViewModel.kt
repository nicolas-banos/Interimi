package com.interimi.interimi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interimi.interimi.network.OpenAIRequest
import com.interimi.interimi.network.OpenAIResponse
import com.interimi.interimi.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {

    private val _openAIResponse = MutableStateFlow<String>("")
    val openAIResponse: StateFlow<String> = _openAIResponse

    fun askOpenAI(question: String) {
        val request = OpenAIRequest(
            messages = listOf(mapOf("role" to "user", "content" to question))
        )

        RetrofitInstance.api.getChatCompletion(request).enqueue(object : Callback<OpenAIResponse> {
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
