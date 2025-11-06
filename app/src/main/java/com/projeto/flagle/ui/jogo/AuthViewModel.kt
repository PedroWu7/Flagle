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

/**
 * Estado da UI para a tela de autenticação.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedInUser: FirebaseUser? = null,
    // --- NOVOS CAMPOS DE ESTADO ---
    val email: String = "",
    val password: String = "",
    val nome: String = "", // Usado apenas no registro
    val isRegisterMode: Boolean = false // Alterna entre Login e Registro
    // --- FIM DOS NOVOS CAMPOS ---
)

/**
 * ViewModel para gerenciar o processo de login e estado do usuário.
 */
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

    /**
     * Chamado pela UI (LoginScreen) após o GoogleSignInClient retornar o idToken.
     */
    /* // <-- LÓGICA DO GOOGLE REMOVIDA
    fun signInWithGoogleToken(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 1. Tenta fazer login no Firebase Auth
                val user = authRepository.firebaseSignInWithGoogle(idToken)

                // 2. (ETAPA 5) Se for bem-sucedido, cria/atualiza o perfil no Firestore
                userRepository.criarPerfilDeUsuario(user) // <-- Esta função também mudou

                // 3. Atualiza o estado da UI com o usuário logado
                _uiState.update { it.copy(isLoading = false, loggedInUser = user) }

            } catch (e: Exception) {
                // Em caso de erro, atualiza a UI com a mensagem de erro
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Erro desconhecido") }
            }
        }
    }
    */ // <-- FIM DA REMOÇÃO

    // --- NOVAS FUNÇÕES ---
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

    /**
     * Chamado pelo botão principal (Entrar/Registrar) na UI.
     */
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
                    // --- FLUXO DE REGISTRO ---
                    // 1. Cria o usuário no Auth
                    val newUser = authRepository.registerWithEmail(state.email, state.password)
                    // 2. Cria o perfil no Firestore
                    userRepository.criarPerfilDeUsuario(newUser, state.nome)
                    newUser
                } else {
                    // --- FLUXO DE LOGIN ---
                    authRepository.signInWithEmail(state.email, state.password)
                }
                // 3. Atualiza a UI
                _uiState.update { it.copy(isLoading = false, loggedInUser = user) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Erro desconhecido") }
            }
        }
    }
    // --- FIM DAS NOVAS FUNÇÕES ---


    /**
     * Limpa a mensagem de erro da UI.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Faz logout do usuário.
     */
    fun signOut() {
        authRepository.signOut()
        _uiState.update { AuthUiState() } // Reseta para o estado inicial
    }
}