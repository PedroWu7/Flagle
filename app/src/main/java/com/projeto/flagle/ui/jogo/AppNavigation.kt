package com.projeto.flagle.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projeto.flagle.data.local.AppDatabase
import com.projeto.flagle.data.repository.AuthRepository
import com.projeto.flagle.data.repository.BandeirasRepository
import com.projeto.flagle.data.repository.UserRepository
import com.projeto.flagle.ui.auth.AuthViewModel
import com.projeto.flagle.ui.auth.AuthViewModelFactory
import com.projeto.flagle.ui.auth.LoginScreen
import com.projeto.flagle.ui.jogo.BandeirasViewModel
import com.projeto.flagle.ui.jogo.BandeirasViewModelFactory
import com.projeto.flagle.ui.jogo.TelaCadastroBandeiras
import com.projeto.flagle.ui.jogo.TelaJogo
import com.projeto.flagle.ui.pontuacao.TelaPontuacao
import com.projeto.flagle.ui.ranking.RankingViewModel
import com.projeto.flagle.ui.ranking.RankingViewModelFactory
import com.projeto.flagle.ui.ranking.TelaRanking
import com.projeto.flagle.ui.theme.FlagleTheme

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // --- Configuração dos Repositórios e Factories ---
    val context = LocalContext.current

    // Repositório de Bandeiras (Room)
    val bandeirasRepository = remember {
        BandeirasRepository(AppDatabase.getDatabase(context).bandeirasDAO())
    }
    // Repositórios do Firebase (Singleton)
    val userRepository = remember { UserRepository() }
    val authRepository = remember { AuthRepository() }

    // Factory do AuthViewModel
    val authViewModelFactory = remember {
        AuthViewModelFactory(authRepository, userRepository)
    }
    // Factory do BandeirasViewModel (agora precisa do UserRepository)
    val bandeirasViewModelFactory = remember {
        BandeirasViewModelFactory(bandeirasRepository, userRepository)
    }
    // --- NOVO: Factory do RankingViewModel ---
    val rankingViewModelFactory = remember {
        RankingViewModelFactory(userRepository, bandeirasRepository)
    }

    // Pega o AuthViewModel no nível mais alto da navegação
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    val authState by authViewModel.uiState.collectAsState()

    // --- Fim da Configuração ---

    // Define a tela inicial com base no estado de login
    val startDestination = if (authState.loggedInUser != null) "jogo" else "login"

    FlagleTheme(
        dynamicColor = false
    ) {
        NavHost(navController = navController, startDestination = startDestination) {

            // --- Rota de Login ---
            composable("login") {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        // Navega para o jogo e limpa a pilha de volta
                        navController.navigate("jogo") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            // --- Rota do Jogo ---
            composable("jogo") {
                // Pega o BandeirasViewModel aqui
                val bandeirasViewModel: BandeirasViewModel = viewModel(factory = bandeirasViewModelFactory)

                TelaJogo(
                    viewModel = bandeirasViewModel,
                    onNavigateToCadastro = {
                        navController.navigate("cadastro")
                    },
                    onNavigateToRanking = {
                        navController.navigate("ranking")
                    },
                    onNavigateToPontuacao = {
                        navController.navigate("pontuacao")
                    },
                    // --- ATUALIZADO: Passa a função de logout ---
                    onSignOut = {
                        authViewModel.signOut()
                        // Navega de volta ao login e limpa a pilha
                        navController.navigate("login") {
                            popUpTo("jogo") { inclusive = true }
                        }
                    }
                )
            }

            // --- Rota de Cadastro de Bandeiras ---
            composable("cadastro") {
                // Pega o mesmo BandeirasViewModel para compartilhar o estado
                val bandeirasViewModel: BandeirasViewModel = viewModel(factory = bandeirasViewModelFactory)

                TelaCadastroBandeiras(
                    viewModel = bandeirasViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // --- Rota do Ranking ---
            composable("ranking") {
                // --- NOVO: Pega o RankingViewModel aqui ---
                val rankingViewModel: RankingViewModel = viewModel(factory = rankingViewModelFactory)

                TelaRanking(
                    viewModel = rankingViewModel, // Passa o ViewModel
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // --- Rota de Pontuação ---
            composable("pontuacao") {
                TelaPontuacao(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}