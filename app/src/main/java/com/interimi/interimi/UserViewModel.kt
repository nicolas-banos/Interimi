package com.interimi.interimi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState

    fun getUserById(userId: Int) {
        viewModelScope.launch {
            _userState.value = userDao.getUserById(userId)
        }
    }

    fun insertUser(user: User, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val id = userDao.insertUser(user)
            onComplete(id)
        }
    }

    fun updateUserGoals(userId: Int, newGoal: String) {
        viewModelScope.launch {
            val user = userDao.getUserById(userId)
            val updatedGoals = (user?.goals ?: "").plus("\n- $newGoal") // Agrega nueva meta con formato "- "
            userDao.updateUserGoals(userId, updatedGoals.trim()) // Guardar metas actualizadas
            getUserById(userId) // Recargar usuario
        }
    }
}
