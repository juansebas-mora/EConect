package com.econect.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.econect.app.data.local.db.entity.RouteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(route: RouteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(routes: List<RouteEntity>)

    @Query("SELECT * FROM routes WHERE id = :id")
    suspend fun getById(id: String): RouteEntity?

    @Query("SELECT * FROM routes WHERE recyclerId = :recyclerId")
    fun observeByRecycler(recyclerId: String): Flow<List<RouteEntity>>

    @Query("SELECT * FROM routes WHERE recyclerId = :recyclerId AND date = :date")
    fun observeByRecyclerAndDate(recyclerId: String, date: String): Flow<List<RouteEntity>>

    @Query("SELECT * FROM routes WHERE status = :status")
    fun observeByStatus(status: String): Flow<List<RouteEntity>>

    @Query("DELETE FROM routes WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM routes")
    suspend fun deleteAll()
}
