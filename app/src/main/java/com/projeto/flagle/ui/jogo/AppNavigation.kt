package com.projeto.flagle.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState // --- NOVO ---
import androidx.compose.runtime.getValue // --- NOVO ---
import androidx.compose.runtime.remember // --- NOVO ---
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projeto.flagle.data.local.AppDatabase
import com.projeto.flagle.data.repository.BandeirasRepository
// --- NOVOS IMPORTS ---
import com.projeto.flagle.data.repository.AuthRepository
import com.projeto.flagle.data.repository.UserRepository
import com.projeto.flagle.ui.auth.AuthViewModel
import com.projeto.flagle.ui.auth.AuthViewModelFactory
import com.projeto.flagle.ui.auth.LoginScreen
// --- FIM DOS NOVOS IMPORTS ---
import com.projeto.flagle.ui.jogo.BandeirasViewModel
import com.projeto.flagle.ui.jogo.BandeirasViewModelFactory
import com.projeto.flagle.ui.jogo.TelaCadastroBandeiras
import com.projeto.flagle.ui.jogo.TelaJogo
import com.projeto.flagle.ui.pontuacao.TelaPontuacao
import com.projeto.flagle.ui.ranking.TelaRanking
// Importando seu tema e os esquemas de cores
import com.projeto.flagle.ui.theme.FlagleTheme
import com.projeto.flagle.ui.theme.DarkColorScheme
import com.projeto.flagle.ui.theme.LightColorScheme

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current

    // --- REPOSITÓRIOS ---
    val repository = BandeirasRepository(AppDatabase.getDatabase(context).bandeirasDAO())
    // --- NOVOS ---
    // (Use 'remember' para que eles não sejam recriados em cada recomposição)
    val userRepository = remember { UserRepository() }
    val authRepository = remember { AuthRepository() }


    // --- FACTORIES ---
    // Factory do Jogo (MODIFICADA para incluir o userRepository)
    val bandeirasViewModelFactory = BandeirasViewModelFactory(repository, userRepository)
    // Factory de Autenticação (NOVA)
    val authViewModelFactory = AuthViewModelFactory(authRepository, userRepository)

    // --- VIEWMODELS (No escopo da Navegação) ---
    // ViewModel do Jogo (como estava)
    val viewModel: BandeirasViewModel = viewModel(factory = bandeirasViewModelFactory)
    // ViewModel de Autenticação (NOVO)
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    val authState by authViewModel.uiState.collectAsState()


    // Usando o seu FlagleTheme para aplicar o tema
    FlagleTheme(
        dynamicColor = false // Desativando a cor dinâmica para forçar o Light/DarkColorScheme
    ) {

        // --- DESTINO INICIAL DINÂMICO (MODIFICADO) ---
        // Verifica se o usuário está logado para decidir a tela inicial
        val startDestination = if (authState.loggedInUser != null) "jogo" else "login"

        NavHost(navController = navController, startDestination = startDestination) {

            // --- ROTA DE LOGIN (NOVA) ---
            composable("login") {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        // Navega para o jogo e limpa a pilha de login
                        navController.navigate("jogo") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("jogo") {
                TelaJogo(
                    viewModel = viewModel,
                    onNavigateToCadastro = {
                        navController.navigate("cadastro")
                    },
                    onNavigateToRanking = {
                        navController.navigate("ranking")
                    },
                    onNavigateToPontuacao = {
                        navController.navigate("pontuacao")
                    },
                    // --- AÇÃO RECOMENDADA ---
                    // Adicione um botão de "Sair" na sua TelaJogo
                    // e passe este callback para ele:
                    /*
                    onSignOut = {
                        authViewModel.signOut()
                        navController.navigate("login") {
                            popUpTo("jogo") { inclusive = true }
                        }
                    }
                    */
                )
            }


            composable("cadastro") {
                TelaCadastroBandeiras(
                    viewModel = viewModel, // Reutiliza o mesmo ViewModel, correto!
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("ranking") {
                TelaRanking(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("pontuacao") {
                TelaPontuacao(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    } // Fecha o FlagleTheme
}