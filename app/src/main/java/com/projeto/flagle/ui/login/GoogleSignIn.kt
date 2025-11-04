package com.projeto.flagle.ui.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase

import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.material3.Button // Para o Composable Button
import androidx.compose.material3.Text // Para o Composable Text
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth // Importação da função de extensão 'auth'
import androidx.compose.runtime.Composable
import com.projeto.flagle.R

@Composable
fun GoogleSignInButton(
    onSignInSuccess: (FirebaseUser) -> Unit,
    onSignInFailure: (Exception) -> Unit
) {
    val context = LocalContext.current
    val firebaseAuth = Firebase.auth

    // 1. Configurar as opções de login Google
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // Importante!
            .requestEmail()
            .build()
    }

    // 2. Criar o cliente de login Google
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // 3. Activity Result Launcher para lidar com o resultado do login
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!

            // 4. Usar o token para autenticar no Firebase
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        onSignInSuccess(firebaseAuth.currentUser!!)
                    } else {
                        onSignInFailure(authTask.exception!!)
                    }
                }
        } catch (e: ApiException) {
            onSignInFailure(e)
        }
    }

    Button(onClick = {
        // Inicia o fluxo de login Google
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }) {
        Text("Login com Google")
    }
}

