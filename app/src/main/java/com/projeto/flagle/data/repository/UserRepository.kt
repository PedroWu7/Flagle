package com.projeto.flagle.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions // <-- IMPORT NECESSÁRIO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Classe de dados para representar a estrutura do usuário no Firestore.
 */
data class UsuarioRankingData(
    val id: String = "",
    val nome: String = "",
    val pontosTotais: Long = 0,
    val pontosPorContinente: Map<String, Long> = emptyMap()
)

/**
 * Repositório para gerenciar os dados dos usuários no Cloud Firestore.
 */
class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usuariosCollection = db.collection("usuarios")

    /**
     * Retorna o ID do usuário logado ou null se não houver usuário.
     */
    private fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    /**
     * Cria ou atualiza o perfil de um usuário no Firestore após o login ou registro.
     */
    suspend fun criarPerfilDeUsuario(user: FirebaseUser, nome: String) {
        val userRef = usuariosCollection.document(user.uid)

        // Dados iniciais do usuário
        val userData = hashMapOf(
            "nome" to nome,
            "email" to user.email,
            "pontosTotais" to 0,
            "pontosPorContinente" to emptyMap<String, Int>(),
            "nivelDeAcesso" to "usuario" // Nível de acesso padrão
        )

        try {
            // Usamos set com merge para criar ou mesclar, sem sobrescrever pontos
            userRef.set(userData, SetOptions.merge()).await()
            Log.d("UserRepository", "Perfil do usuário criado/atualizado com sucesso.")
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao criar perfil de usuário: ${e.message}", e)
            // Lança o erro para o ViewModel saber
            throw e
        }
    }

    /**
     * Atualiza a pontuação do usuário no Firestore.
     * Esta é uma operação de transação para garantir consistência.
     */
    suspend fun atualizarPontos(continente: String, pontosGanhos: Int) {
        val userId = getCurrentUserId() ?: run {
            Log.w("UserRepository", "Usuário não logado. Não é possível atualizar pontos.")
            return // Retorna se não houver usuário logado
        }

        val userRef = usuariosCollection.document(userId)

        try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)

                // Pega os valores atuais
                val pontosTotaisAntigos = snapshot.getLong("pontosTotais") ?: 0

                // Garante que os pontos não fiquem negativos
                val novoTotal = (pontosTotaisAntigos + pontosGanhos).coerceAtLeast(0)

                // Prepara os dados para salvar
                val dadosParaSalvar = mutableMapOf<String, Any>(
                    "pontosTotais" to novoTotal
                )

                // Só atualiza o mapa de continentes se os pontos forem positivos
                if (pontosGanhos > 0) {
                    // O Firestore armazena números como Long, então lemos como Map<String, Long>
                    val pontosPorContinenteAntigo = (snapshot.get("pontosPorContinente") as? Map<String, Long>) ?: emptyMap()
                    val novoPorContinente = pontosPorContinenteAntigo.toMutableMap()

                    // Garante que o continente está em maiúsculas
                    val continenteUpper = continente.uppercase()

                    val pontosAtuaisContinente = novoPorContinente[continenteUpper] ?: 0
                    // Atualiza os pontos do continente, garantindo que não seja negativo
                    novoPorContinente[continenteUpper] = (pontosAtuaisContinente + pontosGanhos).coerceAtLeast(0)

                    dadosParaSalvar["pontosPorContinente"] = novoPorContinente
                }

                // *** A CORREÇÃO CRÍTICA ***
                // Usa set com merge para CRIAR o documento (se for o primeiro acerto)
                // ou ATUALIZAR os campos se ele já existir.
                transaction.set(userRef, dadosParaSalvar, SetOptions.merge())
                // *** FIM DA CORREÇÃO ***

                null // Transação retorna null
            }.await() // Espera a transação completar
            Log.d("UserRepository", "Pontuação ($pontosGanhos) atualizada com sucesso para o usuário $userId.")

        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao atualizar pontuação: ${e.message}", e)
            // Não relança a exceção, falha silenciosa (mas loga o erro)
        }
    }

    /**
     * Busca o ranking geral de usuários e escuta atualizações em tempo real.
     * Usado pela TelaRanking.
     */
    fun getRankingGeralListener(): Flow<List<UsuarioRankingData>> = callbackFlow {
        // Ordena por pontosTotais em ordem decrescente
        val listener = usuariosCollection
            .orderBy("pontosTotais", Query.Direction.DESCENDING)
            .limit(50) // Limita aos 50 melhores
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("UserRepository", "Erro ao ouvir o ranking.", error)
                    // Fecha o flow com o erro
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val rankingList = snapshots.documents.mapNotNull { doc ->
                        // Converte o documento do Firestore para a nossa classe de dados
                        UsuarioRankingData(
                            id = doc.id,
                            nome = doc.getString("nome") ?: "Jogador Anônimo",
                            pontosTotais = doc.getLong("pontosTotais") ?: 0,
                            pontosPorContinente = (doc.get("pontosPorContinente") as? Map<String, Long>) ?: emptyMap()
                        )
                    }
                    // Envia a nova lista para o flow
                    trySend(rankingList)
                }
            }

        // Quando o flow for cancelado, remove o listener
        awaitClose { listener.remove() }
    }
}