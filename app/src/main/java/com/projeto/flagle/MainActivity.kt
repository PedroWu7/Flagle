package com.projeto.flagle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.projeto.flagle.ui.AppNavigation
import com.projeto.flagle.ui.jogo.TelaJogo
import com.projeto.flagle.ui.theme.FlagleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlagleTheme {
                AppNavigation()
            }
        }
    }
}