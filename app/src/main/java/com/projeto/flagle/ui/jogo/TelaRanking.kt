package com.projeto.flagle.ui.ranking // Novo pacote

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// --- Dados Fictícios para o Esqueleto ---
data class UsuarioRanking(val nome: String, val pontuacao: Int, val continente: String)

// Lista de usuários fictícia
val usuariosFicticios = listOf(
    UsuarioRanking("Jogador_1", 1500, "EUROPA"),
    UsuarioRanking("Jogador_2", 1450, "AMÉRICA"),
    UsuarioRanking("Jogador_3", 1300, "ÁSIA"),
    UsuarioRanking("Jogador_4", 1200, "EUROPA"),
    UsuarioRanking("Jogador_5", 1100, "ÁFRICA"),
    UsuarioRanking("Jogador_6", 1050, "AMÉRICA"),
    UsuarioRanking("Jogador_7", 900, "OCEANIA"),
).sortedByDescending { it.pontuacao } // Já deixa ordenado

// Lista de continentes fictícia
val continentesFicticios = listOf("TODOS", "AMÉRICA", "EUROPA", "ÁSIA", "ÁFRICA", "OCEANIA")
// --- Fim dos Dados Fictícios ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRanking(
    onNavigateBack: () -> Unit
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Geral", "Por Continente")

    var continenteSelecionado by remember { mutableStateOf(continentesFicticios.first()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ranking") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Abas para "Geral" e "Por Continente"
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Conteúdo baseado na aba selecionada
            when (tabIndex) {
                // Aba "Geral"
                0 -> RankingList(
                    usuarios = usuariosFicticios
                )
                // Aba "Por Continente"
                1 -> RankingPorContinente(
                    continenteSelecionado = continenteSelecionado,
                    onContinenteChange = { continenteSelecionado = it },
                    usuarios = usuariosFicticios
                )
            }
        }
    }
}

/**
 * Exibe uma lista de ranking de usuários.
 */
@Composable
fun RankingList(usuarios: List<UsuarioRanking>, modifier: Modifier = Modifier) {
    if (usuarios.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Nenhum jogador encontrado.", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(usuarios.size) { index ->
            val usuario = usuarios[index]
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${index + 1}. ${usuario.nome}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${usuario.pontuacao} pts",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Exibe o dropdown de continentes e a lista filtrada.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingPorContinente(
    continenteSelecionado: String,
    onContinenteChange: (String) -> Unit,
    usuarios: List<UsuarioRanking>,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    // Filtro fictício (no futuro, isso viria do ViewModel)
    val usuariosFiltrados = remember(continenteSelecionado, usuarios) {
        if (continenteSelecionado == "TODOS") {
            usuarios
        } else {
            usuarios.filter { it.continente.equals(continenteSelecionado, ignoreCase = true) }
        }
        // A lista principal já está ordenada, então o sub-filtro também estará.
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dropdown para selecionar o continente
        ExposedDropdownMenuBox(
            expanded = isMenuExpanded,
            onExpandedChange = { isMenuExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = continenteSelecionado.uppercase(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Selecionar Continente") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMenuExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false }
            ) {
                continentesFicticios.forEach { continente ->
                    DropdownMenuItem(
                        text = { Text(continente.uppercase()) },
                        onClick = {
                            onContinenteChange(continente)
                            isMenuExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de ranking filtrada
        RankingList(usuarios = usuariosFiltrados)
    }
}
