package com.interimi.interimi.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interimi.interimi.domain.usecases.UseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OpenAIViewModel @Inject constructor(
    private val useCases: UseCases
) : ViewModel() {

    private val _openAIResponse = MutableStateFlow("Esperando respuesta...")
    val openAIResponse: StateFlow<String> = _openAIResponse

    fun askAI(question: String, userId: Int) {
        viewModelScope.launch {
            _openAIResponse.value = "Cargando..."
            _openAIResponse.value = useCases.askOpenAI(question, userId)
        }
    }
}

