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
import com.projeto.flagle.ui.viewmodel.AuthViewModel
import com.projeto.flagle.ui.viewmodel.AuthViewModelFactory
import com.projeto.flagle.ui.auth.LoginScreen
import com.projeto.flagle.ui.viewmodel.BandeirasViewModel
import com.projeto.flagle.ui.viewmodel.BandeirasViewModelFactory
import com.projeto.flagle.ui.telas.TelaCadastroBandeiras
import com.projeto.flagle.ui.telas.TelaJogo
import com.projeto.flagle.ui.pontuacao.TelaPontuacao
import com.projeto.flagle.ui.viewmodel.RankingViewModel
import com.projeto.flagle.ui.viewmodel.RankingViewModelFactory
import com.projeto.flagle.ui.ranking.TelaRanking
import com.projeto.flagle.ui.theme.FlagleTheme

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Configuração dos Repositórios e Factories
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
    // Factory do BandeirasViewModel
    val bandeirasViewModelFactory = remember {
        BandeirasViewModelFactory(bandeirasRepository, userRepository)
    }
    // Factory do RankingViewModel
    val rankingViewModelFactory = remember {
        RankingViewModelFactory(userRepository, bandeirasRepository)
    }

    // Pega o AuthViewModel no nivel mais alto da navegacao
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    val authState by authViewModel.uiState.collectAsState()


    // muda a tela inicial com base no estado de login
    val startDestination = if (authState.loggedInUser != null) "jogo" else "login"

    FlagleTheme(
        dynamicColor = false
    ) {
        NavHost(navController = navController, startDestination = startDestination) {

            // Rota de login
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


            composable("jogo") {
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

                    onSignOut = {
                        authViewModel.signOut()
                        navController.navigate("login") {
                            popUpTo("jogo") { inclusive = true }
                        }
                    }
                )
            }

            composable("cadastro") {
                val bandeirasViewModel: BandeirasViewModel = viewModel(factory = bandeirasViewModelFactory)

                TelaCadastroBandeiras(
                    viewModel = bandeirasViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }


            composable("ranking") {
                val rankingViewModel: RankingViewModel = viewModel(factory = rankingViewModelFactory)

                TelaRanking(
                    viewModel = rankingViewModel,
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
    }
}