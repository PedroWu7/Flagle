package com.projeto.flagle.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bandeiras")
data class Bandeiras(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val url_imagem: String,
    val continente: String
)
