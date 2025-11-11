package com.projeto.flagle.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.projeto.flagle.data.repository.AuthRepository
import com.projeto.flagle.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedInUser: FirebaseUser? = null,
    val email: String = "",
    val password: String = "",
    val nome: String = "",
    val isRegisterMode: Boolean = false // alternar entre Login e Registro

)


  //gerenciar o processo de login e estado do usuario

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Verifica se o usuário já está logado ao iniciar o ViewModel
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            _uiState.update { it.copy(loggedInUser = currentUser) }
        }
    }



    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onNameChange(nome: String) {
        _uiState.update { it.copy(nome = nome) }
    }

    fun toggleRegisterMode() {
        _uiState.update { it.copy(isRegisterMode = !it.isRegisterMode, error = null) }
    }


    fun performAuthentication() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Email e senha não podem estar em branco.") }
            return
        }

        if (state.isRegisterMode && state.nome.isBlank()) {
            _uiState.update { it.copy(error = "O nome é obrigatório para o registro.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = if (state.isRegisterMode) {
                    val newUser = authRepository.registerWithEmail(state.email, state.password)
                    userRepository.criarPerfilDeUsuario(newUser, state.nome)
                    newUser
                } else {
                    authRepository.signInWithEmail(state.email, state.password)
                }

                _uiState.update { it.copy(isLoading = false, loggedInUser = user) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Erro desconhecido") }
            }
        }
    }




    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }


    fun signOut() {
        authRepository.signOut()
        _uiState.update { AuthUiState() } // Reseta para o estado inicial
    }
}