package com.interimi.interimi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interimi.interimi.data.PreferencesRepository
import com.interimi.interimi.data.UserRepository
import com.interimi.interimi.domain.usecases.UseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserPreferencesViewModel @Inject constructor(
    private val useCases: UseCases
) : ViewModel() {

    private val _userHistory = MutableStateFlow("")
    val userHistory: StateFlow<String> = _userHistory

    fun loadHistory() {
        viewModelScope.launch {
            useCases.getUserHistory().collect { history ->
                _userHistory.value = history
            }
        }
    }

    fun saveHistory(newEntry: String) {
        viewModelScope.launch {
            val updatedHistory = _userHistory.value + "\n- $newEntry"
            useCases.saveUserHistory(updatedHistory)
            _userHistory.value = updatedHistory
        }
    }
}

