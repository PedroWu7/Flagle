package com.projeto.flagle.ui.jogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.projeto.flagle.data.local.Bandeiras
import com.projeto.flagle.data.repository.BandeirasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BandeirasUiState(
    // --- Estado do Cadastro ---
    val listaDeBandeiras: List<Bandeiras> = emptyList(),
    val nome: String = "",
    val url_imagem: String = "",
    val continente: String = "",
    val bandeirasEmEdicao: Bandeiras? = null,

    // --- Estado do Jogo (para TelaJogo) ---
    val bandeiraSorteada: Bandeiras? = null,
    val palpiteUsuario: String = "",
    val mensagemResultado: String = "",

    // NOVO: Controla se o primeiro carregamento/sorteio já foi feito
    val carregamentoInicialCompleto: Boolean = false
) {
    val textoBotao: String
        get() = if (bandeirasEmEdicao == null) "Adicionar Bandeira" else "Atualizar Bandeira"
}

class BandeirasViewModel(private val repository: BandeirasRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(BandeirasUiState())
    val uiState: StateFlow<BandeirasUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.buscarTodos().collect { bandeiras ->
                _uiState.update { currentState ->
                    // Se a lista carregada não estiver vazia E o sorteio inicial ainda não aconteceu
                    if (bandeiras.isNotEmpty() && !currentState.carregamentoInicialCompleto) {
                        currentState.copy(
                            listaDeBandeiras = bandeiras,
                            bandeiraSorteada = bandeiras.random(), // Faz o primeiro sorteio AQUI
                            carregamentoInicialCompleto = true, // Marca como feito
                            mensagemResultado = "" // Limpa qualquer mensagem de erro anterior
                        )
                    }
                    // Se a lista estiver vazia (seja no início ou depois de deletar tudo)
                    else if (bandeiras.isEmpty() && !currentState.carregamentoInicialCompleto) {
                        currentState.copy(
                            listaDeBandeiras = bandeiras,
                            bandeiraSorteada = null,
                            mensagemResultado = "Nenhuma bandeira cadastrada!",
                            carregamentoInicialCompleto = true // Marca como feito, mesmo vazio
                        )
                    }
                    // Se a lista for atualizada mas o sorteio inicial já foi feito (ex: cadastro de nova bandeira)
                    else {
                        currentState.copy(
                            listaDeBandeiras = bandeiras
                        )
                    }
                }
            }
        }
    }

    // --- Funções da Tela de Cadastro ---

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
            repository.deletar(bandeira)
        }
    }

    fun onSalvar() {
        val state = _uiState.value
        if (state.nome.isBlank() || state.url_imagem.isBlank() || state.continente.isBlank()) return

        val bandeirasParaSalvar = state.bandeirasEmEdicao?.copy(
            nome = state.nome.uppercase(),
            url_imagem = state.url_imagem,
            continente = state.continente.uppercase()
        ) ?: Bandeiras( // Se não... cria um novo...
            nome = state.nome.uppercase(),
            url_imagem = state.url_imagem,
            continente = state.continente.uppercase()
        )
        viewModelScope.launch {
            if (state.bandeirasEmEdicao == null) { // Verifica se está editando ou adicionando
                repository.inserir(bandeirasParaSalvar) // Adiciona um novo filme
            } else {
                repository.atualizar(bandeirasParaSalvar) // Atualiza um filme existente
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


    // --- Funções da Tela de Jogo ---

    /**
     * Atualiza o palpite do usuário no state.
     */
    fun onPalpiteChange(palpite: String) {
        _uiState.update { it.copy(palpiteUsuario = palpite) }
    }

    /**
     * Sorteia uma nova bandeira da lista e limpa o palpite/resultado anterior.
     * (Usado pelo botão "Pular")
     */
    fun sortearNovaBandeira() {
        val lista = _uiState.value.listaDeBandeiras
        val bandeiraAtual = _uiState.value.bandeiraSorteada

        // Se a lista tem 1 ou 0 itens, não há para onde pular.
        if (lista.size <= 1) {
            _uiState.update {
                it.copy(
                    mensagemResultado = if (lista.isEmpty()) "Nenhuma bandeira cadastrada!" else "Apenas uma bandeira cadastrada.",
                    palpiteUsuario = ""
                )
            }
            return // Sai da função
        }

        // --- LÓGICA ATUALIZADA ---
        // Sorteia até encontrar uma bandeira DIFERENTE da atual
        var novaBandeira: Bandeiras
        do {
            novaBandeira = lista.random()
        } while (novaBandeira.nome == bandeiraAtual?.nome) // Garante que a nova bandeira seja diferente (usando o nome como ID único)

        _uiState.update {
            it.copy(
                bandeiraSorteada = novaBandeira, // Seta a nova bandeira
                palpiteUsuario = "",
                mensagemResultado = "" // Limpa a mensagem
            )
        }
    }

    /**
     * Verifica se o palpite do usuário é igual ao nome da bandeira sorteada.
     */
    fun verificarPalpite() {
        val palpite = _uiState.value.palpiteUsuario.trim()
        val nomeCorreto = _uiState.value.bandeiraSorteada?.nome?.trim()

        if (nomeCorreto == null) {
            _uiState.update { it.copy(mensagemResultado = "Nenhuma bandeira sorteada.") }
            return
        }

        // Compara ignorando maiúsculas/minúsculas
        if (palpite.equals(nomeCorreto, ignoreCase = true)) {
            _uiState.update { it.copy(mensagemResultado = "Correto! Parabéns!") }
        } else {
            _uiState.update { it.copy(mensagemResultado = "Errado! Tente novamente.") }
        }
    }
}

class BandeirasViewModelFactory(private val repository: BandeirasRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BandeirasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BandeirasViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

