package com.econect.app.domain.repository

import com.econect.app.domain.model.RecyclingCenter
import com.econect.app.domain.model.Result

interface RecyclingCenterRepository {
    suspend fun getAll(): Result<List<RecyclingCenter>>
    suspend fun getById(id: String): Result<RecyclingCenter?>
    suspend fun save(recyclingCenter: RecyclingCenter): Result<Unit>
    suspend fun getByOwnerUid(ownerUid: String): Result<RecyclingCenter?>
}
