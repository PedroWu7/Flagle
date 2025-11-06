package com.projeto.flagle.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
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
    suspend fun firebaseSignInWithGoogle(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        // .await() é uma função de extensão das coroutines (vamos adicionar a dependência)
        val authResult = auth.signInWithCredential(credential).await()
        return authResult.user!!
    }

    /**
     * Faz logout do usuário atual.
     */
    fun signOut() {
        auth.signOut()
    }
}