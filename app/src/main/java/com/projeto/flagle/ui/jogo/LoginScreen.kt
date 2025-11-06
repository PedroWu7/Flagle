package com.projeto.flagle.ui.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.projeto.flagle.R // Importe o R do seu projeto

/**
 * Tela de Login que gerencia o fluxo de Google Sign-In.
 *
 * @param authViewModel O ViewModel que gerencia o estado de autenticação.
 * @param onLoginSuccess Um callback para notificar o navegador que o login foi bem-sucedido.
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 1. (ETAPA 4) Configurar o GoogleSignInClient
    val googleSignInClient: GoogleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // O R.string.default_web_client_id é pego automaticamente do seu google-services.json
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // 2. (ETAPA 4) Criar o Launcher para o resultado do login
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Sucesso! Pegamos a conta do Google
                val account = task.getResult(ApiException::class.java)!!

                // Pegamos o idToken e enviamos para o ViewModel
                account.idToken?.let { token ->
                    Log.d("LoginScreen", "Token do Google recebido, enviando para o ViewModel.")
                    authViewModel.signInWithGoogleToken(token)
                } ?: run {
                    Log.e("LoginScreen", "Token do Google é nulo.")
                    authViewModel.clearError() // Limpa erro antigo se houver
                }
            } catch (e: ApiException) {
                // Erro ao pegar a conta
                Log.e("LoginScreen", "Falha no Google Sign-In: ${e.statusCode}", e)
                authViewModel.clearError()
            }
        } else {
            Log.w("LoginScreen", "Login cancelado ou falhou. Resultado: ${result.resultCode}")
        }
    }

    // 3. (Navegação) Observar o estado do ViewModel
    LaunchedEffect(uiState.loggedInUser) {
        if (uiState.loggedInUser != null) {
            // Se o usuário está logado, chama o callback para navegar para o jogo
            Log.d("LoginScreen", "Login bem-sucedido! Navegando para o jogo...")
            onLoginSuccess()
        }
    }

    // 4. (UI) Layout da tela de login
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Flagle", style = MaterialTheme.typography.displayMedium)
                Text("Desafie seu conhecimento em bandeiras")
                Spacer(modifier = Modifier.height(32.dp))

                // Botão que inicia o fluxo de login
                Button(
                    onClick = {
                        authViewModel.clearError() // Limpa erros antigos
                        Log.d("LoginScreen", "Iniciando o fluxo de login do Google...")
                        launcher.launch(googleSignInClient.signInIntent)
                    }
                ) {
                    Text("Entrar com o Google")
                }

                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Erro: ${uiState.error}",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}