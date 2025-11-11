package com.projeto.flagle.ui.ranking // Novo pacote

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed // Importamos o itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projeto.flagle.data.repository.UsuarioRankingData // Importamos a classe de dados

// --- DADOS FICTÍCIOS REMOVIDOS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRanking(
    viewModel: RankingViewModel, // Recebe o ViewModel
    onNavigateBack: () -> Unit
) {
    // Pega o estado da UI a partir do ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Geral", "Por Continente")

    var continenteSelecionado by remember { mutableStateOf("TODOS") }

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

            // --- MOSTRA LOADING OU ERRO ---
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.erro != null) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Erro: ${uiState.erro}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                // Conteúdo baseado na aba selecionada
                when (tabIndex) {
                    // Aba "Geral"
                    0 -> RankingList(
                        usuarios = uiState.rankingGeral // <-- Usa dados reais
                    )
                    // Aba "Por Continente"
                    1 -> RankingPorContinente(
                        continenteSelecionado = continenteSelecionado,
                        onContinenteChange = { continenteSelecionado = it },
                        // Passa o mapa de ranking e a lista de continentes
                        rankingPorContinente = uiState.rankingPorContinente,
                        listaContinentes = uiState.listaContinentes
                    )
                }
            }
        }
    }
}

/**
 * Exibe uma lista de ranking de usuários.
 */
@Composable
fun RankingList(usuarios: List<UsuarioRankingData>, modifier: Modifier = Modifier) { // <-- Usa UsuarioRankingData
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
        // Usamos itemsIndexed para pegar o 'index'
        itemsIndexed(usuarios) { index, usuario ->
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
                        text = "${index + 1}. ${usuario.nome}", // <-- Usa dados reais
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${usuario.pontosTotais} pts", // <-- Usa dados reais
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
    rankingPorContinente: Map<String, List<UsuarioRankingData>>, // <-- Recebe o Mapa
    listaContinentes: List<String>, // <-- Recebe a lista de continentes
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    // Pega a lista correta do mapa, ou uma lista vazia se não houver
    val usuariosFiltrados = rankingPorContinente[continenteSelecionado.uppercase()] ?: emptyList()

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
                // Usa a lista de continentes vinda do ViewModel
                listaContinentes.forEach { continente ->
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