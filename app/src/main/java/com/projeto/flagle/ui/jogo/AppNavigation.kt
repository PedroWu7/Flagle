package com.projeto.flagle.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projeto.flagle.data.local.AppDatabase
import com.projeto.flagle.data.repository.BandeirasRepository
import com.projeto.flagle.ui.jogo.BandeirasViewModel
import com.projeto.flagle.ui.jogo.BandeirasViewModelFactory
import com.projeto.flagle.ui.jogo.TelaCadastroBandeiras // Importe sua tela
import com.projeto.flagle.ui.jogo.TelaJogo // Importe sua tela

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val repository = BandeirasRepository(AppDatabase.getDatabase(context).bandeirasDAO())
    val viewModelFactory = BandeirasViewModelFactory(repository)

    // Este ViewModel será compartilhado
    val viewModel: BandeirasViewModel = viewModel(factory = viewModelFactory)

    NavHost(navController = navController, startDestination = "jogo") {

        // --- TELA DE JOGO ---
        composable("jogo") {
            // 1. PASSA O VIEWMODEL COMPARTILHADO
            TelaJogo(
                viewModel = viewModel,
                // 2. PASSA A AÇÃO DE NAVEGAR
                onNavigateToCadastro = {
                    navController.navigate("cadastro")
                }
            )
        }

        // --- TELA DE CADASTRO ---
        composable("cadastro") {
            // 1. PASSA O MESMO VIEWMODEL COMPARTILHADO
            TelaCadastroBandeiras(
                viewModel = viewModel,
                // 2. PASSA A AÇÃO DE VOLTAR
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}