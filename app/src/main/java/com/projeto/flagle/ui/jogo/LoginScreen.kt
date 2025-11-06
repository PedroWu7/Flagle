package com.projeto.flagle.ui.auth

// import android.app.Activity // <-- REMOVIDO
import android.util.Log
// import androidx.activity.compose.rememberLauncherForActivityResult // <-- REMOVIDO
// import androidx.activity.result.contract.ActivityResultContracts // <-- REMOVIDO
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField // <-- NOVO
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton // <-- NOVO
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
// import androidx.compose.runtime.remember // <-- REMOVIDO
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.platform.LocalContext // <-- REMOVIDO
// import androidx.compose.ui.res.stringResource // <-- REMOVIDO
import androidx.compose.ui.text.input.KeyboardType // <-- NOVO
import androidx.compose.ui.text.input.PasswordVisualTransformation // <-- NOVO
import androidx.compose.ui.unit.dp
// import com.google.android.gms.auth.api.signin.GoogleSignIn // <-- REMOVIDO
// import com.google.android.gms.auth.api.signin.GoogleSignInClient // <-- REMOVIDO
// import com.google.android.gms.auth.api.signin.GoogleSignInOptions // <-- REMOVIDO
// import com.google.android.gms.common.api.ApiException // <-- REMOVIDO
// import com.projeto.flagle.R // <-- REMOVIDO (Não é mais necessário para R.string.default_web_client_id)

/**
 * Tela de Login que gerencia o fluxo de Email e Senha.
 * @param onLoginSuccess Um callback para notificar o navegador que o login foi bem-sucedido.
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    // val context = LocalContext.current // <-- REMOVIDO

    // 1. (ETAPA 4) Configurar o GoogleSignInClient
    /* // <-- TODA A LÓGICA DO GOOGLE SIGN-IN FOI REMOVIDA
    val googleSignInClient: GoogleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }
    */

    // 2. (ETAPA 4) Criar o Launcher para o resultado do login
    /* // <-- REMOVIDO
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                account.idToken?.let { token ->
                    Log.d("LoginScreen", "Token do Google recebido, enviando para o ViewModel.")
                    authViewModel.signInWithGoogleToken(token)
                } ?: run {
                    Log.e("LoginScreen", "Token do Google é nulo.")
                    authViewModel.clearError() // Limpa erro antigo se houver
                }
            } catch (e: ApiException) {
                Log.e("LoginScreen", "Falha no Google Sign-In: ${e.statusCode}", e)
                authViewModel.clearError()
            }
        } else {
             Log.w("LoginScreen", "Login cancelado ou falhou. Resultado: ${result.resultCode}")
        }
    }
    */ // <-- FIM DA REMOÇÃO

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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Adiciona padding
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(), // Ocupa a largura
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Flagle", style = MaterialTheme.typography.displayMedium)
                Text(
                    text = if (uiState.isRegisterMode) "Crie sua conta" else "Faça seu login",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(24.dp))

                // --- NOVOS CAMPOS DE TEXTO ---

                if (uiState.isRegisterMode) {
                    OutlinedTextField(
                        value = uiState.nome,
                        onValueChange = { authViewModel.onNameChange(it) },
                        label = { Text("Nome") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { authViewModel.onEmailChange(it) },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { authViewModel.onPasswordChange(it) },
                    label = { Text("Senha") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                // --- FIM DOS NOVOS CAMPOS ---

                // Botão que inicia o fluxo de login
                Button(
                    onClick = {
                        authViewModel.performAuthentication() // <-- CHAMA A NOVA FUNÇÃO
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (uiState.isRegisterMode) "Registrar" else "Entrar")
                }

                TextButton(onClick = { authViewModel.toggleRegisterMode() }) {
                    Text(
                        if (uiState.isRegisterMode) "Já tem uma conta? Entre aqui."
                        else "Não tem uma conta? Registre-se."
                    )
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