package com.econect.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.econect.app.data.local.db.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE recyclerId = :recyclerId ORDER BY completedAt DESC")
    fun observeByRecycler(recyclerId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE citizenId = :citizenId ORDER BY completedAt DESC")
    fun observeByCitizen(citizenId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE routeId = :routeId")
    fun observeByRoute(routeId: String): Flow<List<TransactionEntity>>

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}
