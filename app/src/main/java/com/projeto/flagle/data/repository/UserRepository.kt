package com.projeto.flagle.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usersCollection = db.collection("usuarios")

    /**
     * ETAPA 5: Salvar dados do usuário no Firestore após o login
     * Isso é chamado logo após o usuário fazer o login com o Google pela primeira vez.
     */
    // --- MODIFICADO ---
    // Agora recebemos um 'nome' e não pegamos mais 'photoUrl' do 'user'
    fun criarPerfilDeUsuario(user: FirebaseUser, nome: String) {
        val userData = hashMapOf(
            "nome" to nome, // <-- Usa o nome vindo do parâmetro
            "email" to user.email,
            "fotoPerfil" to "", // <-- Não temos foto no login por email/senha
            "pontosTotais" to 0L, // Usar Long (L) para números no Firestore
            "pontosPorContinente" to hashMapOf<String, Long>(),
            "nivelDeAcesso" to "usuario" // --- NOVO CAMPO ADICIONADO ---
        )

        // Usamos .set() com SetOptions.merge()
        // Isso cria o documento se ele não existir
        // ou atualiza os campos (sem sobrescrever os pontos) se ele já existir.
        usersCollection.document(user.uid)
            .set(userData, SetOptions.merge())
            .addOnFailureListener { e ->
                // Logar o erro, caso ocorra
                println("Erro ao salvar perfil do usuário: $e")
            }
    }


    /**
     * ETAPA 7: Atualizar a pontuação do usuário no jogo
     * Esta função é chamada pelo BandeirasViewModel quando o usuário acerta.
     */
    fun atualizarPontos(uid: String, continente: String, pontosGanhos: Int) {
        val userRef = usersCollection.document(uid)
        val continenteKey = "pontosPorContinente.$continente" // Chave para o mapa aninhado

        // Usamos uma transação para garantir que os dados não sejam corrompidos
        // se o usuário jogar em dois dispositivos ao mesmo tempo.
        db.runTransaction { transaction ->
            // Precisamos ler os dados (transaction.get) antes de poder escrever (transaction.update)
            val snapshot = transaction.get(userRef)

            // Verificamos se o documento existe antes de tentar atualizar
            if (snapshot.exists()) {
                transaction.update(userRef, mapOf(
                    // Incrementa o total de pontos
                    "pontosTotais" to FieldValue.increment(pontosGanhos.toLong()),
                    // Incrementa os pontos no mapa do continente específico
                    continenteKey to FieldValue.increment(pontosGanhos.toLong())
                ))
            } else {
                // Se o documento não existir (caso raro, mas possível),
                // podemos optar por não fazer nada ou tentar criar um perfil básico.
                // Por enquanto, apenas logamos.
                println("Documento do usuário $uid não encontrado para atualizar pontos.")
            }
        }.addOnFailureListener { e ->
            println("Falha na transação de atualização de pontos: $e")
        }
    }
}