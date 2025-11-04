package com.projeto.flagle.ui.jogo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ScoreViewModel : ViewModel() {

    // Expõe a pontuação atual para a UI
    private val _pontuacao = MutableStateFlow(0)
    val pontuacao: StateFlow<Int> = _pontuacao.asStateFlow()

    // Expõe a pontuação mais alta (recorde)
    private val _recorde = MutableStateFlow(0) // No futuro, pode carregar do SharedPreferences
    val recorde: StateFlow<Int> = _recorde.asStateFlow()


    // Adiciona pontos à pontuação atual @param pontos Os pontos a serem adicionados.

    fun adicionarPontos(pontos: Int) {
        _pontuacao.update { pontuacaoAtual -> pontuacaoAtual + pontos }
    }

    //ainda preciso ver como vai ficar o sistema de pontuacao
    //fiz parcial, mas termino
    fun resetarRodada() {
        _pontuacao.value = 0
    }
}
