package com.projeto.flagle.ui.jogo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
// --- Imports Removidos ---
// import androidx.compose.material.icons.outlined.Abc
// import androidx.compose.material.icons.outlined.Settings
// --- Imports Adicionados (já estão na biblioteca padrão) ---
import androidx.compose.material.icons.filled.Settings // Ou .Default
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

/**
 * ATUALIZADO: Layout e estética profissional.
 * Recebe o ViewModel e a função de navegar.
 */
@Composable
fun TelaJogo(
    viewModel: BandeirasViewModel,
    onNavigateToCadastro: () -> Unit // Ação para navegar para a tela de cadastro
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(16.dp) // Padding geral da tela
        ) {
            // --- Conteúdo Principal do Jogo ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter), // Alinha o conteúdo no topo
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "FLAGLE",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 32.dp) // Mais espaço em cima/baixo
                )

                // Bandeira como um "Card" de destaque
                Bandeira(
                    url_imagem = uiState.bandeiraSorteada?.url_imagem ?: "",
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // 90% da largura
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // TextField com estilo "Outlined"
                OutlinedTextField(
                    value = uiState.palpiteUsuario,
                    onValueChange = { viewModel.onPalpiteChange(it) },
                    label = { Text("Qual é esta bandeira?") },
                    modifier = Modifier.fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(12.dp),
                    // --- ÍCONE CORRIGIDO ---
                    leadingIcon = { Icon(Icons.Default.Settings, "Palpite") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Texto de resultado (Correto/Errado)
                val isCorrect = uiState.mensagemResultado.startsWith("Correto")
                val resultColor = if (uiState.mensagemResultado.isEmpty()) {
                    Color.Transparent
                } else if (isCorrect) {
                    Color(0xFF388E3C) // Verde (Material Design)
                } else {
                    MaterialTheme.colorScheme.error // Vermelho (do Tema)
                }

                Text(
                    text = uiState.mensagemResultado,
                    style = MaterialTheme.typography.titleMedium,
                    color = resultColor,
                    modifier = Modifier.height(30.dp) // Reserva espaço
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botões de ação com hierarquia (Principal e Secundário)
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    // Botão secundário (vazado)
                    OutlinedButton(
                        onClick = { viewModel.sortearNovaBandeira() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pular")
                    }
                    // Botão principal (cheio)
                    Button(
                        onClick = { viewModel.verificarPalpite() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Adivinhar")
                    }
                }
            } // Fim da Coluna principal

            // --- Botão de Navegação (Fixo embaixo) ---
            TextButton(
                onClick = onNavigateToCadastro,
                modifier = Modifier
                    .align(Alignment.BottomCenter) // Alinha na base do Box
                    .padding(bottom = 16.dp)
            ) {
                // --- ÍCONE CORRIGIDO ---
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gerenciar Bandeiras")
            }
        }
    }
}

/**
 * Composable da Bandeira atualizado para usar Card.
 */
@Composable
private fun Bandeira(url_imagem: String, modifier: Modifier = Modifier) {
    // Placeholder caso a URL esteja vazia
    val model = if (url_imagem.isEmpty()) {
        "https://placehold.co/300x200/e0e0e0/7f7f7f?text=?"
    } else {
        url_imagem
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp), // Borda arredondada
        elevation = CardDefaults.cardElevation(8.dp), // Sombra
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) // Borda sutil
    ) {
        AsyncImage(
            model = model,
            contentDescription = "Bandeira para adivinhar",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize() // Preenche o Card
        )
    }
}


@Preview
@Composable
fun TelaJogoPreview() {
    // A preview pode não funcionar corretamente, pois agora depende
    // de um ViewModel real sendo injetado.
    // Para testar o layout, você pode criar um ViewModel "fake"
    // ou "mockar" os dados, mas isso é mais complexo.
}

