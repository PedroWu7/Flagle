package com.projeto.flagle.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.projeto.flagle.data.local.Bandeiras

// Esta classe vai centralizar toda a lógica do Firestore (Etapas 5, 6, 7)
class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("usuarios")

    /**
     * ETAPA 5: Salvar dados do usuário no Firestore após o login
     * Isso é chamado logo após o usuário fazer o login com o Google pela primeira vez.
     */
    fun criarPerfilDeUsuario(user: FirebaseUser) {
        val userData = hashMapOf(
            "nome" to user.displayName,
            "email" to user.email,
            "fotoPerfil" to user.photoUrl.toString(),
            "pontosTotais" to 0L, // Usar Long (L) para números no Firestore
            "pontosPorContinente" to hashMapOf<String, Long>()
        )

        usersCollection.document(user.uid)
            .set(userData, SetOptions.merge()) // SetOptions.merge() evita sobrescrever dados existentes
            .addOnSuccessListener {
                Log.d("UserRepository", "Perfil do usuário criado/atualizado no Firestore.")
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Erro ao salvar perfil do usuário", e)
            }
    }

    /**
     * ETAPA 7: Atualizar pontuação no jogo
     * Esta é a função que o BandeirasViewModel vai chamar quando o usuário acertar.
     */
    fun atualizarPontos(uid: String, continente: String, pontosGanhos: Int) {
        val userRef = usersCollection.document(uid)
        val continenteUpper = continente.uppercase() // Garante consistência no nome do continente

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val pontosTotaisAtuais = snapshot.getLong("pontosTotais") ?: 0L
            val pontosPorContinente = snapshot.get("pontosPorContinente") as? HashMap<String, Long> ?: hashMapOf()

            val novoTotal = pontosTotaisAtuais + pontosGanhos

            val novoPorContinente = pontosPorContinente.toMutableMap()
            val pontosAtuaisContinente = novoPorContinente[continenteUpper] ?: 0L
            novoPorContinente[continenteUpper] = pontosAtuaisContinente + pontosGanhos

            // Atualiza os campos no Firestore
            transaction.update(userRef, mapOf(
                "pontosTotais" to novoTotal,
                "pontosPorContinente" to novoPorContinente
            ))

            // Retorna sucesso (necessário para a transação)
            null
        }.addOnSuccessListener {
            Log.d("UserRepository", "Pontuação atualizada com sucesso!")
        }.addOnFailureListener { e ->
            Log.e("UserRepository", "Erro ao atualizar pontuação", e)
        }
    }

    /**
     * ETAPA 6: Ranking de usuários
     * Você pode chamar isso em um ViewModel de Ranking separado.
     * (Estou usando um callback simples aqui, mas você pode usar Flows)
     */
    fun buscarRanking(limite: Long = 10, onComplete: (List<Map<String, Any>>) -> Unit) {
        usersCollection
            .orderBy("pontosTotais", Query.Direction.DESCENDING)
            .limit(limite)
            .get()
            .addOnSuccessListener { documents ->
                val rankingList = mutableListOf<Map<String, Any>>()
                for (doc in documents) {
                    rankingList.add(doc.data)
                }
                onComplete(rankingList)
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Erro ao buscar ranking", e)
                onComplete(emptyList())
            }
    }
}