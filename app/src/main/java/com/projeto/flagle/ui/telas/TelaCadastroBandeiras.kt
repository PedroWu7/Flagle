package com.projeto.flagle.ui.telas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.projeto.flagle.data.local.Bandeiras
import com.projeto.flagle.ui.viewmodel.BandeirasViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastroBandeiras(
    viewModel: BandeirasViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(

        modifier = Modifier.fillMaxSize(),

        topBar = {
            TopAppBar(
                title = { Text("Cadastrar Bandeiras") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )

            ) {

                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = uiState.nome,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = { Text("Nome da Bandeira") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    TextField(
                        value = uiState.url_imagem,
                        onValueChange = { viewModel.onUrlImagemChange(it) },
                        label = { Text("Url da Imagem") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = uiState.continente,
                        onValueChange = { viewModel.onContinenteChange(it) },
                        label = { Text("Continente do País") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier.align(Alignment.End),
                        onClick = { viewModel.onSalvar() }
                    ) {
                        Text(uiState.textoBotao)
                    }
                }
            }

            LazyColumn {
                items(uiState.listaDeBandeiras) { bandeira ->
                    UmaBandeira(
                        bandeira = bandeira,
                        onEdit = { viewModel.onEditar(bandeira) },
                        onDelete = { viewModel.onDeletar(bandeira) }
                    )
                }
            }
        }
    }
}

@Composable
fun UmaBandeira(bandeira: Bandeiras, onEdit: (Bandeiras) -> Unit, onDelete: (Bandeiras) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(text = bandeira.nome, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = bandeira.url_imagem,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                )
                Text(text = bandeira.continente, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onEdit(bandeira)
                    }
            )
            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Deletar",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onDelete(bandeira)
                    }
            )
        }
    }
}



@Preview
@Composable
fun TelaCadastroBandeirasPreview() {

}

