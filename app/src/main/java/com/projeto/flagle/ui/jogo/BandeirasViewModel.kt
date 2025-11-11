package com.projeto.flagle.ui.jogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.projeto.flagle.data.local.Bandeiras
import com.projeto.flagle.data.repository.BandeirasRepository
import com.projeto.flagle.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class BandeirasUiState(
    val listaDeBandeiras: List<Bandeiras> = emptyList(),
    val nome: String = "",
    val url_imagem: String = "",
    val continente: String = "",
    val bandeirasEmEdicao: Bandeiras? = null,
    val bandeiraSorteada: Bandeiras? = null,
    val palpiteUsuario: String = "",
    val mensagemResultado: String = "",
    val carregamentoInicialCompleto: Boolean = false,

    val continenteSelecionado: String = "TODOS",
    val listaContinentes: List<String> = listOf("TODOS"),

    val quadradosRevelados: Int = 0,
    val modoDificil: Boolean = true
) {
    val textoBotao: String
        get() = if (bandeirasEmEdicao == null) "Adicionar Bandeira" else "Atualizar Bandeira"
}


class BandeirasViewModel(
    private val repository: BandeirasRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BandeirasUiState())
    val uiState: StateFlow<BandeirasUiState> = _uiState.asStateFlow()

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    init {
        viewModelScope.launch {
            repository.buscarTodos().collect { bandeiras ->
                _uiState.update { currentState ->
                    val continentes = listOf("TODOS") + bandeiras
                        .map { it.continente.uppercase() }
                        .distinct()
                        .sorted()

                    if (!currentState.carregamentoInicialCompleto) {
                        val bandeiraInicial = if (bandeiras.isNotEmpty()) bandeiras.random() else null
                        val mensagem = if (bandeiras.isEmpty()) "Nenhuma bandeira cadastrada!" else ""
                        val quadradosIniciais = if (currentState.modoDificil) 0 else 6

                        currentState.copy(
                            listaDeBandeiras = bandeiras,
                            bandeiraSorteada = bandeiraInicial,
                            carregamentoInicialCompleto = true,
                            mensagemResultado = mensagem,
                            listaContinentes = continentes,
                            quadradosRevelados = quadradosIniciais
                        )
                    } else {
                        currentState.copy(
                            listaDeBandeiras = bandeiras,
                            listaContinentes = continentes
                        )
                    }
                }
            }
        }
    }

    fun onNameChange(novoNome: String) {
        _uiState.update { it.copy(nome = novoNome.uppercase()) }
    }

    fun onUrlImagemChange(novaUrl: String) {
        _uiState.update { it.copy(url_imagem = novaUrl) }
    }

    fun onContinenteChange(continente: String) {
        _uiState.update { it.copy(continente = continente.uppercase()) }
    }

    fun onEditar(bandeira: Bandeiras) {
        _uiState.update {
            it.copy(
                bandeirasEmEdicao = bandeira,
                nome = bandeira.nome.uppercase(),
                url_imagem = bandeira.url_imagem,
                continente = bandeira.continente.uppercase()
            )
        }
    }

    fun onDeletar(bandeira: Bandeiras) {
        viewModelScope.launch {
            val bandeiraAtual = _uiState.value.bandeiraSorteada
            repository.deletar(bandeira)
            if (bandeiraAtual?.nome == bandeira.nome) {
                sortearNovaBandeira()
            }
        }
    }

    fun onSalvar() {
        val state = _uiState.value
        if (state.nome.isBlank() || state.url_imagem.isBlank() || state.continente.isBlank()) return

        val bandeirasParaSalvar = state.bandeirasEmEdicao?.copy(
            nome = state.nome.uppercase(),
            url_imagem = state.url_imagem,
            continente = state.continente.uppercase()
        ) ?: Bandeiras(
            nome = state.nome.uppercase(),
            url_imagem = state.url_imagem,
            continente = state.continente.uppercase()
        )
        viewModelScope.launch {
            if (state.bandeirasEmEdicao == null) {
                repository.inserir(bandeirasParaSalvar)
            } else {
                repository.atualizar(bandeirasParaSalvar)
            }
        }
        LimparCampos()
    }

    private fun LimparCampos() {
        _uiState.update {
            it.copy(
                nome = "",
                url_imagem = "",
                continente = "",
                bandeirasEmEdicao = null
            )
        }
    }

    fun onModoDificuldadeChange(isDificil: Boolean) {
        _uiState.update { it.copy(modoDificil = isDificil) }
        sortearNovaBandeira()
    }

    fun onPalpiteChange(palpite: String) {
        _uiState.update { it.copy(palpiteUsuario = palpite) }
    }

    fun onContinenteSelecionadoChange(continente: String) {
        _uiState.update { it.copy(continenteSelecionado = continente) }
        sortearNovaBandeira()
    }

    fun sortearNovaBandeira() {
        val state = _uiState.value
        val listaCompleta = state.listaDeBandeiras
        val filtro = state.continenteSelecionado
        val bandeiraAtual = state.bandeiraSorteada

        val listaFiltrada = if (filtro == "TODOS") {
            listaCompleta
        } else {
            listaCompleta.filter { it.continente.equals(filtro, ignoreCase = true) }
        }

        val quadradosIniciais = if (state.modoDificil) 0 else 6

        if (listaFiltrada.isEmpty()) {
            _uiState.update {
                it.copy(
                    bandeiraSorteada = null,
                    mensagemResultado = if (filtro == "TODOS") "Nenhuma bandeira cadastrada!" else "Nenhuma bandeira para o continente '$filtro'.",
                    palpiteUsuario = "",
                    quadradosRevelados = quadradosIniciais
                )
            }
            return
        }

        if (listaFiltrada.size == 1 && listaFiltrada.first().nome == bandeiraAtual?.nome) {
            _uiState.update {
                it.copy(
                    mensagemResultado = "Apenas uma bandeira para este continente.",
                    palpiteUsuario = "",
                    quadradosRevelados = quadradosIniciais
                )
            }
            return
        }

        var novaBandeira: Bandeiras
        do {
            novaBandeira = listaFiltrada.random()
        } while (listaFiltrada.size > 1 && novaBandeira.nome == bandeiraAtual?.nome)

        _uiState.update {
            it.copy(
                bandeiraSorteada = novaBandeira,
                palpiteUsuario = "",
                mensagemResultado = "", // limpa a mensagem ao pular/sortear
                quadradosRevelados = quadradosIniciais
            )
        }
    }


    fun onPularClick() {
        val state = _uiState.value
        val userId = currentUserId
        val continente = state.bandeiraSorteada?.continente ?: "DESCONHECIDO"
        val pontosPerdidos = -2

        if (userId != null) {
            viewModelScope.launch {
                userRepository.atualizarPontos(continente, pontosPerdidos)
            }
        }


        sortearNovaBandeira()
    }


    private fun calcularPontosGanhos(state: BandeirasUiState): Int {
        if (!state.modoDificil) {
            return 2 // Modo facil
        }

        // Modo Dificil
        return when (state.quadradosRevelados) {
            0 -> 6
            1 -> 5
            2 -> 4
            3 -> 3
            4 -> 2
            5 -> 1
            else -> 1
        }
    }

    fun verificarPalpite() {
        val state = _uiState.value
        val palpite = state.palpiteUsuario.trim()
        val nomeCorreto = state.bandeiraSorteada?.nome?.trim()

        if (nomeCorreto == null) {
            val filtro = state.continenteSelecionado
            val mensagem = if (filtro == "TODOS") "Nenhuma bandeira cadastrada!" else "Nenhuma bandeira para o continente '$filtro'."
            _uiState.update { it.copy(mensagemResultado = mensagem) }
            return
        }

        if (palpite.equals(nomeCorreto, ignoreCase = true)) {
            val userId = currentUserId
            val continente = state.bandeiraSorteada?.continente ?: "DESCONHECIDO"
            val pontosGanhos = calcularPontosGanhos(state)


            if (userId != null) {
                viewModelScope.launch {
                    userRepository.atualizarPontos(continente, pontosGanhos)
                }
            }


            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        mensagemResultado = "Correto! +$pontosGanhos pontos!",
                        quadradosRevelados = 6
                    )
                }
                delay(1500L)
                sortearNovaBandeira()
            }
        } else {
            if (state.modoDificil) {
                val novosQuadrados = (state.quadradosRevelados + 1).coerceAtMost(6)
                _uiState.update {
                    it.copy(
                        mensagemResultado = "Errado! Tente novamente.",
                        quadradosRevelados = novosQuadrados
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        mensagemResultado = "Errado! Tente novamente."
                    )
                }
            }
        }
    }
}

class BandeirasViewModelFactory(
    private val repository: BandeirasRepository,
    private val userRepository: UserRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BandeirasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BandeirasViewModel(repository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}