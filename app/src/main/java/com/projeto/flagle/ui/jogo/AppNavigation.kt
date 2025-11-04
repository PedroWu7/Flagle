package com.projeto.flagle.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable // --- IMPORTANTE ---
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projeto.flagle.data.local.AppDatabase
import com.projeto.flagle.data.repository.BandeirasRepository
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
    val repository = BandeirasRepository(AppDatabase.getDatabase(context).bandeirasDAO())
    val viewModelFactory = BandeirasViewModelFactory(repository)

    val viewModel: BandeirasViewModel = viewModel(factory = viewModelFactory)



    // Usando o seu FlagleTheme para aplicar o tema
    FlagleTheme(
        dynamicColor = false // Desativando a cor dinâmica para forçar o Light/DarkColorScheme
    ) {
        NavHost(navController = navController, startDestination = "jogo") {

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
                )
            }


            composable("cadastro") {
                TelaCadastroBandeiras(
                    viewModel = viewModel,
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

