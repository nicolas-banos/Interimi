package com.interimi.interimi.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interimi.interimi.User
import com.interimi.interimi.domain.usecases.UseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val useCases: UseCases
) : ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState

    fun getUserById(userId: Int) {
        viewModelScope.launch {
            _userState.value = useCases.getUserById(userId)
        }
    }

    fun insertUser(user: User, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val id = useCases.insertUser(user)
            onComplete(id)
            getUserById(user.id)
        }
    }

    fun updateUserGoals(userId: Int, newGoal: String) {
        viewModelScope.launch {
            useCases.updateUserGoals(userId, newGoal)
            getUserById(userId)
        }
    }

    fun deleteGoalsByUserId(userId: Int){
        viewModelScope.launch {
            useCases.deleteGoalsByUserId(userId)
            getUserById(userId)
        }
    }
}




