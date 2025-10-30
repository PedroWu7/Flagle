package com.projeto.flagle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Preview
@Composable
fun TelaJogo() {
    Scaffold {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Text(
                "FLAGLE",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(10.dp)
            )
            Bandeira("https://globalsherpa.org/wp-content/uploads/2012/02/brazil-flag.jpg")
            TextField(
                value = "Digite o nome da bandeira: ",
                onValueChange = {},
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
private fun Bandeira(url_imagem: String) {
    AsyncImage(
        model = url_imagem,
        contentDescription = "bandeira",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(300.dp)
            .height(200.dp)
    )
}