package com.econect.app.domain.repository

import com.econect.app.domain.model.RecyclingCenter
import com.econect.app.domain.model.Result

interface RecyclingCenterRepository {
    suspend fun getAll(): Result<List<RecyclingCenter>>
    suspend fun getById(id: String): Result<RecyclingCenter?>
}
