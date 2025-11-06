package com.projeto.flagle.ui.jogo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star // Adicionado (Padrão)
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaJogo(
    viewModel: BandeirasViewModel,
    onNavigateToCadastro: () -> Unit,
    onNavigateToRanking: () -> Unit,
    onNavigateToPontuacao: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- ÍCONES DA BOTTOM BAR CORRIGIDOS ---
                TextButton(onClick = onNavigateToRanking) {
                    Icon(
                        Icons.Default.Star, // Ícone correto (Padrão)
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ranking")
                }
                TextButton(onClick = onNavigateToPontuacao) {
                    Icon(
                        Icons.Default.Info, // Ícone correto
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pontos")
                }
                TextButton(onClick = onNavigateToCadastro) {
                    Icon(
                        Icons.Default.Settings, // Ícone correto
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bandeiras")
                }
            }
        }
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(16.dp)
        ) {
            // --- BOTÃO DE TEMA CORRIGIDO ---


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "FLAGLE",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 32.dp)
                )

                // Botões de Dificuldade
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val modoFacil = uiState.modoDificil == false
                    OutlinedButton(
                        onClick = { viewModel.onModoDificuldadeChange(false) },
                        modifier = Modifier.weight(1f),
                        border = if (modoFacil) BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.primary
                        ) else ButtonDefaults.outlinedButtonBorder
                    ) {
                        Text("Fácil")
                    }
                    OutlinedButton(
                        onClick = { viewModel.onModoDificuldadeChange(true) },
                        modifier = Modifier.weight(1f),
                        border = if (!modoFacil) BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.primary
                        ) else ButtonDefaults.outlinedButtonBorder
                    ) {
                        Text("Difícil")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))


                // --- Dropdown para selecionar o continente ---
                var isMenuExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = isMenuExpanded,
                    onExpandedChange = { isMenuExpanded = it },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    OutlinedTextField(
                        value = uiState.continenteSelecionado.uppercase(),
                        onValueChange = {}, // O valor é controlado pelo ViewModel
                        readOnly = true,
                        label = { Text("Continente") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMenuExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor() // Necessário para o dropdown
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }
                    ) {
                        uiState.listaContinentes.forEach { continente ->
                            DropdownMenuItem(
                                text = { Text(continente.uppercase()) },
                                onClick = {
                                    viewModel.onContinenteSelecionadoChange(continente)
                                    isMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                // --- FIM do Dropdown ---

                Spacer(modifier = Modifier.height(16.dp))

                Bandeira(
                    url_imagem = uiState.bandeiraSorteada?.url_imagem ?: "",
                    quadradosRevelados = uiState.quadradosRevelados,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = uiState.palpiteUsuario.uppercase(),
                    onValueChange = { viewModel.onPalpiteChange(it) },
                    label = { Text("Qual é esta bandeira?") },
                    modifier = Modifier.fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))


                val isCorrect = uiState.mensagemResultado.startsWith("Correto")
                val resultColor = if (uiState.mensagemResultado.isEmpty()) {
                    Color.Transparent
                } else if (isCorrect) {
                    Color(0xFF388E3C)
                } else {
                    MaterialTheme.colorScheme.error
                }

                Text(
                    text = uiState.mensagemResultado,
                    style = MaterialTheme.typography.titleMedium,
                    color = resultColor,
                    modifier = Modifier.height(30.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.sortearNovaBandeira() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pular")
                    }
                    Button(
                        onClick = { viewModel.verificarPalpite() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Adivinhar")
                    }
                }
                // Adiciona espaço extra na parte inferior para garantir que o scroll funcione
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}


@Composable
private fun Bandeira(
    url_imagem: String,
    quadradosRevelados: Int,
    modifier: Modifier = Modifier
) {
    val model = if (url_imagem.isEmpty()) {
        "https://placehold.co/300x200/e0e0e0/7f7f7f?text=?"
    } else {
        url_imagem
    }

    val indicesEmbaralhados = remember(url_imagem) { (0..5).toList().shuffled() }

    val indicesVisiveis = remember(quadradosRevelados, indicesEmbaralhados) {
        // Se for 6 (modo fácil ou acerto), mostra tudo.
        if (quadradosRevelados >= 6) {
            (0..5).toSet()
        } else {
            indicesEmbaralhados.take(quadradosRevelados).toSet()
        }
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = model,
                contentDescription = "Bandeira para adivinhar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Grade de cobertura 2x3
            Column(modifier = Modifier.fillMaxSize()) {
                repeat(2) { rowIndex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        repeat(3) { colIndex ->
                            val quadradoIndex = (rowIndex * 3) + colIndex
                            val isVisivel = indicesVisiveis.contains(quadradoIndex)

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)))
                            ) {
                                if (!isVisivel) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${quadradoIndex + 1}",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.4f
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TelaJogoPreview() {
    // Preview estático que não depende do ViewModel
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text("FLAGLE", style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            OutlinedButton(onClick = { /* */ }, modifier = Modifier.weight(1f)) {
                Text("Fácil")
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(onClick = { /* */ }, modifier = Modifier.weight(1f)) {
                Text("Difícil")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "TODOS",
            onValueChange = {},
            readOnly = true,
            label = { Text("Continente") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text("Placeholder Bandeira")
        }
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Qual é esta bandeira?") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Errado! Tente novamente.",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            OutlinedButton(onClick = { /* */ }, modifier = Modifier.weight(1f)) {
                Text("Pular")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { /* */ }, modifier = Modifier.weight(1f)) {
                Text("Adivinhar")
            }
        }
    }
}