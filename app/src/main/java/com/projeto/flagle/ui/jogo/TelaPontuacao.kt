package com.projeto.flagle.ui.pontuacao

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Nova tela que exibe o sistema de pontuação.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPontuacao(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sistema de Pontuação") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RuleCard(
                title = "Modo Fácil",
                description = "Acertar a bandeira no modo fácil (bandeira toda revelada).",
                points = "+2 Pontos"
            )

            RuleCard(
                title = "Modo Difícil (Perfeito)",
                description = "Acertar a bandeira na primeira tentativa (sem erros).",
                points = "+6 Pontos"
            )

            RuleCard(
                title = "Modo Difícil (Com erros)",
                description = "A cada erro (quadrado revelado), 1 ponto é descontado da pontuação máxima. (Ex: Acertar na 2ª tentativa = 5 pontos; na 3ª = 4 pontos, etc.)",
                points = "+1 a +5 Pontos"
            )
        }
    }
}

/**
 * Um Card para exibir uma regra de pontuação.
 */
@Composable
private fun RuleCard(title: String, description: String, points: String, isPenalty: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = points,
                style = MaterialTheme.typography.titleMedium,
                color = if (isPenalty) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
    }
}
