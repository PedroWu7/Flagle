package com.projeto.flagle.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
// import com.google.firebase.auth.GoogleAuthProvider // <-- REMOVIDO
import kotlinx.coroutines.tasks.await

/**
 * Repositório para lidar com a lógica de autenticação do Firebase.
 */
class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Pega o usuário atualmente logado no Firebase.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Tenta fazer login no Firebase usando um ID Token do Google.
     * Retorna o FirebaseUser em caso de sucesso.
     * Dispara uma exceção em caso de falha.
     */
    /* // <-- LÓGICA DO GOOGLE REMOVIDA
    suspend fun firebaseSignInWithGoogle(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        // .await() é uma função de extensão das coroutines (vamos adicionar a dependência)
        val authResult = auth.signInWithCredential(credential).await()
        return authResult.user!!
    }
    */ // <-- FIM DA REMOÇÃO

    // --- NOVO ---
    /**
     * Tenta fazer login no Firebase com email e senha.
     * Retorna o FirebaseUser em caso de sucesso.
     * Dispara uma exceção em caso de falha.
     */
    suspend fun signInWithEmail(email: String, password: String): FirebaseUser {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        return authResult.user!!
    }

    /**
     * Tenta registrar um novo usuário no Firebase com email e senha.
     * Retorna o FirebaseUser em caso de sucesso.
     * Dispara uma exceção em caso de falha.
     */
    suspend fun registerWithEmail(email: String, password: String): FirebaseUser {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        return authResult.user!!
    }
    // --- FIM DO NOVO ---


    /**
     * Faz logout do usuário atual.
     */
    fun signOut() {
        auth.signOut()
    }
}