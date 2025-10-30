package com.projeto.flagle.data.repository

import com.projeto.flagle.data.local.Bandeiras
import com.projeto.flagle.data.local.BandeirasDAO
import kotlinx.coroutines.flow.Flow

class BandeirasRepository (private val bandeirasDAO: BandeirasDAO) {
    suspend fun buscarTodos(): Flow<List<Bandeiras>> {
        return bandeirasDAO.buscarTodos()
    }

    suspend fun inserir(filme: Bandeiras){
        bandeirasDAO.inserir(filme)
    }

    suspend fun atualizar(filme: Bandeiras){
        bandeirasDAO.atualizar(filme)
    }

    suspend fun deletar(filme: Bandeiras){
        bandeirasDAO.deletar(filme)
    }
}