package com.econect.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.econect.app.data.local.db.entity.RecyclableMaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecyclableMaterialDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(material: RecyclableMaterialEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(materials: List<RecyclableMaterialEntity>)

    @Query("SELECT * FROM recyclable_materials WHERE id = :id")
    suspend fun getById(id: String): RecyclableMaterialEntity?

    @Query("SELECT * FROM recyclable_materials WHERE citizenId = :citizenId")
    fun observeByCitizen(citizenId: String): Flow<List<RecyclableMaterialEntity>>

    @Query("SELECT * FROM recyclable_materials WHERE status = :status")
    fun observeByStatus(status: String): Flow<List<RecyclableMaterialEntity>>

    @Query("DELETE FROM recyclable_materials WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM recyclable_materials")
    suspend fun deleteAll()
}
