package com.projeto.flagle.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.projeto.flagle.data.repository.BandeirasRepository
import com.projeto.flagle.data.repository.UserRepository
import com.projeto.flagle.data.repository.UsuarioRankingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class RankingUiState(
    val isLoading: Boolean = true,
    val rankingGeral: List<UsuarioRankingData> = emptyList(),
    val rankingPorContinente: Map<String, List<UsuarioRankingData>> = emptyMap(),
    val listaContinentes: List<String> = listOf("TODOS"),
    val erro: String? = null
)


class RankingViewModel(
    private val userRepository: UserRepository,
    private val bandeirasRepository: BandeirasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    init {
        carregarDadosRanking()
    }

    private fun carregarDadosRanking() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }

            try {

                val continentesFlow = bandeirasRepository.buscarTodos()


                userRepository.getRankingGeralListener().collect { usuarios ->

                    val rankingGeralOrdenado = usuarios.sortedByDescending { it.pontosTotais }
                    val rankingPorContinente = processarRankingPorContinente(usuarios)


                    continentesFlow.collect { bandeiras ->
                        val continentes = listOf("TODOS") + bandeiras
                            .map { it.continente.uppercase() }
                            .distinct()
                            .sorted()

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                rankingGeral = rankingGeralOrdenado,
                                rankingPorContinente = rankingPorContinente,
                                listaContinentes = continentes
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, erro = "Falha ao carregar ranking: ${e.message}") }
            }
        }
    }


    private fun processarRankingPorContinente(usuarios: List<UsuarioRankingData>): Map<String, List<UsuarioRankingData>> {
        val mapa = mutableMapOf<String, MutableList<UsuarioRankingData>>()


        mapa["TODOS"] = usuarios.sortedByDescending { it.pontosTotais }.toMutableList()


        for (usuario in usuarios) {
            for ((continente, pontos) in usuario.pontosPorContinente) {
                if (pontos > 0) {
                    val listaContinente = mapa.getOrPut(continente.uppercase()) { mutableListOf() }


                    listaContinente.add(
                        usuario.copy(

                            pontosTotais = pontos
                        )
                    )
                }
            }
        }

        return mapa.mapValues { (_, lista) ->
            lista.sortedByDescending { it.pontosTotais }
        }
    }
}


class RankingViewModelFactory(
    private val userRepository: UserRepository,
    private val bandeirasRepository: BandeirasRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RankingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RankingViewModel(userRepository, bandeirasRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}