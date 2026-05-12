package com.econect.app.domain.usecase

import com.econect.app.domain.model.Transaction
import com.econect.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecyclerTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(recyclerId: String): Flow<List<Transaction>> =
        repository.getTransactionsByRecycler(recyclerId)
}
