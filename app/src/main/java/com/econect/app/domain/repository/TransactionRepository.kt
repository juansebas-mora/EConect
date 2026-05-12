package com.econect.app.domain.repository

import com.econect.app.domain.model.Result
import com.econect.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    /**
     * Emite las transacciones del reciclador desde caché Room (offline-first).
     * Llamar [syncRecyclerTransactions] para refrescar desde Firestore.
     */
    fun getTransactionsByRecycler(recyclerId: String): Flow<List<Transaction>>

    /** Descarga transacciones del reciclador desde Firestore y actualiza Room. */
    suspend fun syncRecyclerTransactions(recyclerId: String): Result<Unit>
}
