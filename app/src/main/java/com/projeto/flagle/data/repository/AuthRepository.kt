package com.projeto.flagle.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import kotlinx.coroutines.tasks.await


class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }


    suspend fun signInWithEmail(email: String, password: String): FirebaseUser {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        return authResult.user!!
    }


    suspend fun registerWithEmail(email: String, password: String): FirebaseUser {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        return authResult.user!!
    }



    fun signOut() {
        auth.signOut()
    }
}