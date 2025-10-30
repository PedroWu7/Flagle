package com.projeto.flagle.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BandeirasDAO{
    @Insert
    suspend fun inserir(bandeira: Bandeiras)

    @Query("SELECT * FROM bandeiras")
    fun buscarTodos() : Flow<List<Bandeiras>>

    @Delete
    suspend fun deletar(bandeiras: Bandeiras)

    @Update
    suspend fun atualizar(bandeiras: Bandeiras)
}
