package com.example.mangavision.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangavision.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            _isUserLoggedIn.value = authRepository.isUserLoggedIn()
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            val success = authRepository.signIn(email, password)
            if (success) {
                _isUserLoggedIn.value = true
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _isUserLoggedIn.value = false
        }
    }
}