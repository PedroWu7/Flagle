package com.projeto.flagle.ui.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.projeto.flagle.data.repository.BandeirasRepository
import com.projeto.flagle.data.repository.UserRepository
import com.projeto.flagle.data.repository.UsuarioRankingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado da UI para a Tela de Ranking
data class RankingUiState(
    val isLoading: Boolean = true,
    val rankingGeral: List<UsuarioRankingData> = emptyList(),
    val rankingPorContinente: Map<String, List<UsuarioRankingData>> = emptyMap(),
    val listaContinentes: List<String> = listOf("TODOS"),
    val erro: String? = null
)

/**
 * ViewModel para a TelaRanking.
 * Busca os dados de ranking do UserRepository e os dados de continentes do BandeirasRepository.
 */
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
                // 1. Busca a lista de continentes do repositório de bandeiras
                val continentesFlow = bandeirasRepository.buscarTodos()

                // 2. Busca os dados de ranking do repositório de usuário
                // Usamos onSnapshot para ouvir em tempo real
                userRepository.getRankingGeralListener().collect { usuarios ->

                    // 3. Processa os dados
                    val rankingGeralOrdenado = usuarios.sortedByDescending { it.pontosTotais }
                    val rankingPorContinente = processarRankingPorContinente(usuarios)

                    // 4. Combina com a lista de continentes
                    // (Isso é feito dentro do 'collect' do ranking para garantir que temos os usuários primeiro)
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

    /**
     * Processa a lista de usuários para criar o ranking por continente.
     */
    private fun processarRankingPorContinente(usuarios: List<UsuarioRankingData>): Map<String, List<UsuarioRankingData>> {
        val mapa = mutableMapOf<String, MutableList<UsuarioRankingData>>()

        // Adiciona todos os usuários a uma lista "TODOS"
        mapa["TODOS"] = usuarios.sortedByDescending { it.pontosTotais }.toMutableList()

        // Processa por continente
        for (usuario in usuarios) {
            for ((continente, pontos) in usuario.pontosPorContinente) {
                if (pontos > 0) {
                    // Pega a lista do continente ou cria uma nova
                    val listaContinente = mapa.getOrPut(continente.uppercase()) { mutableListOf() }

                    // Adiciona o usuário com a pontuação *daquele* continente
                    listaContinente.add(
                        usuario.copy(
                            // Sobrescreve pontosTotais com os pontos do continente para o ranking
                            pontosTotais = pontos
                        )
                    )
                }
            }
        }

        // Ordena todas as listas do mapa
        return mapa.mapValues { (_, lista) ->
            lista.sortedByDescending { it.pontosTotais }
        }
    }
}

/**
 * Factory para criar o RankingViewModel com as dependências necessárias.
 */
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