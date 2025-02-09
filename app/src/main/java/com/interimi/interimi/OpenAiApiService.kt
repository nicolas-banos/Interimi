package com.interimi.interimi.network

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

data class OpenAIRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Map<String, String>>
)

data class OpenAIResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

data class Message(
    val role: String,
    val content: String
)

interface OpenAIApiService {
    @POST("chat/completions")
    fun getChatCompletion(@Body request: OpenAIRequest): Call<OpenAIResponse>
}
