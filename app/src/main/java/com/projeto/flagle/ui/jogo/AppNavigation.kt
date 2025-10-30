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
import com.projeto.flagle.ui.jogo.TelaCadastroBandeiras
import com.projeto.flagle.ui.jogo.TelaJogo

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val repository = BandeirasRepository(AppDatabase.getDatabase(context).bandeirasDAO())
    val viewModelFactory = BandeirasViewModelFactory(repository)

    val viewModel: BandeirasViewModel = viewModel(factory = viewModelFactory)

    NavHost(navController = navController, startDestination = "jogo") {

        composable("jogo") {
            TelaJogo(
                viewModel = viewModel,
                onNavigateToCadastro = {
                    navController.navigate("cadastro")
                }
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
    }
}