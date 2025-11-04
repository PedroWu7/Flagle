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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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

// Adicionamos a anotação OptIn para usar o ExposedDropdownMenuBox
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaJogo(
    viewModel: BandeirasViewModel,
    onNavigateToCadastro: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "FLAGLE",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    // --- MODIFICADO ---
                    modifier = Modifier.padding(top = 32.dp) // Reduzido padding vertical
                    // --- FIM DA MODIFICAÇÃO ---
                )

                // --- INÍCIO: SELETOR DE DIFICULDADE ---
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botão FÁCIL
                    val isFacil = uiState.modoDificuldade == "FACIL"
                    val facilButton: @Composable () -> Unit = {
                        Text("FÁCIL")
                    }

                    if (isFacil) {
                        Button(onClick = { /* Já selecionado */ }, modifier = Modifier.weight(1f)) {
                            facilButton()
                        }
                    } else {
                        OutlinedButton(onClick = { viewModel.onModoDificuldadeChange("FACIL") }, modifier = Modifier.weight(1f)) {
                            facilButton()
                        }
                    }

                    // Botão DIFÍCIL
                    val isDificil = uiState.modoDificuldade == "DIFICIL"
                    val dificilButton: @Composable () -> Unit = {
                        Text("DIFÍCIL")
                    }

                    if (isDificil) {
                        Button(onClick = { /* Já selecionado */ }, modifier = Modifier.weight(1f)) {
                            dificilButton()
                        }
                    } else {
                        OutlinedButton(onClick = { viewModel.onModoDificuldadeChange("DIFICIL") }, modifier = Modifier.weight(1f)) {
                            dificilButton()
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                // --- FIM: SELETOR DE DIFICULDADE ---


                // --- Dropdown para selecionar o continente ---
                var isMenuExpanded by remember { mutableStateOf(false) }

                // --- CÓDIGO RESTAURADO ---
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
                // --- FIM DO CÓDIGO RESTAURADO ---

                Spacer(modifier = Modifier.height(16.dp))

                // --- CÓDIGO RESTAURADO ---
                Bandeira(
                    url_imagem = uiState.bandeiraSorteada?.url_imagem ?: "",
                    quadradosRevelados = uiState.quadradosRevelados, // Passa o novo estado
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(200.dp)
                )
                // --- FIM DO CÓDIGO RESTAURADO ---

                Spacer(modifier = Modifier.height(32.dp))

                // --- CÓDIGO RESTAURADO ---
                OutlinedTextField(
                    value = uiState.palpiteUsuario.uppercase(),
                    onValueChange = { viewModel.onPalpiteChange(it) },
                    label = { Text("Qual é esta bandeira?") },
                    modifier = Modifier.fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Settings, "Palpite") },
                    singleLine = true
                )
                // --- FIM DO CÓDIGO RESTAURADO ---

                Spacer(modifier = Modifier.height(16.dp))

                // --- CÓDIGO RESTAURADO ---
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
                // --- FIM DO CÓDIGO RESTAURADO ---

                Spacer(modifier = Modifier.height(16.dp))

                // --- CÓDIGO RESTAURADO E CORRIGIDO ---
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.sortearNovaBandeira() }, // <-- ERRO CORRIGIDO
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pular")
                    }
                    Button(
                        onClick = { viewModel.verificarPalpite() }, // <-- CÓDIGO RESTAURADO
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Adivinhar")
                    }
                }
                // --- FIM DO CÓDIGO RESTAURADO ---
            }

            // --- CÓDIGO RESTAURADO ---
            TextButton(
                onClick = onNavigateToCadastro,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {

                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gerenciar Bandeiras")
            }
            // --- FIM DO CÓDIGO RESTAURADO ---
        }
    }
}


// --- COMPOSABLE DA BANDEIRA (CÓDIGO RESTAURADO) ---
@Composable
private fun Bandeira(
    url_imagem: String,
    quadradosRevelados: Int, // Novo parâmetro
    modifier: Modifier = Modifier
) {
    val model = if (url_imagem.isEmpty()) {
        "https://placehold.co/300x200/e0e0e0/7f7f7f?text=?"
    } else {
        url_imagem
    }

    // 1. Cria uma ordem de revelação embaralhada que é mantida
    //    enquanto a URL da imagem não mudar.
    val indicesEmbaralhados = remember(url_imagem) { (0..5).toList().shuffled() }

    // 2. Determina quais índices (0-5) devem estar visíveis
    //    com base no número de quadrados revelados.
    //    Esta lógica já funciona:
    //    - Modo Fácil: quadradosRevelados = 6 -> take(6) -> Mostra todos
    //    - Modo Difícil: quadradosRevelados = 0 -> take(0) -> Mostra 0
    val indicesVisiveis = remember(quadradosRevelados, indicesEmbaralhados) {
        indicesEmbaralhados.take(quadradosRevelados).toSet()
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. A IMAGEM (fica no fundo)
            AsyncImage(
                model = model,
                contentDescription = "Bandeira para adivinhar",
                contentScale = ContentScale.Crop, // Crop é importante para preencher
                modifier = Modifier.fillMaxSize()
            )

            // 2. A GRADE DE COBERTURA (fica na frente da imagem)
            Column(modifier = Modifier.fillMaxSize()) {
                // 2x3 Grid (2 linhas)
                repeat(2) { rowIndex -> // rowIndex = 0, 1
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // Cada linha ocupa metade da altura
                    ) {
                        // 3 colunas
                        repeat(3) { colIndex -> // colIndex = 0, 1, 2
                            // Calcula o índice do quadrado (de 0 a 5)
                            val quadradoIndex = (rowIndex * 3) + colIndex
                            val isVisivel = indicesVisiveis.contains(quadradoIndex)

                            // O Quadrado de cobertura
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f) // Cada coluna ocupa 1/3 da largura
                                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)))
                            ) {
                                // Se NÃO estiver visível, desenha a cobertura
                                if (!isVisivel) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surfaceVariant), // Cor da cobertura
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Número do quadrado (opcional, mas ajuda o usuário)
                                        Text(
                                            text = "${quadradoIndex + 1}",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.4f
                                            )
                                        )
                                    }
                                }
                                // Se ESTIVER visível, não desenha nada,
                                // revelando a imagem que está no fundo.
                            }
                        }
                    }
                }
            }
        }
    }
}
// --- FIM DO CÓDIGO RESTAURADO ---


@Preview
@Composable
fun TelaJogoPreview() {

}

