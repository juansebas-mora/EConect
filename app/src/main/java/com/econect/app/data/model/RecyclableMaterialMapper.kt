package com.econect.app.data.model

import com.econect.app.data.local.db.entity.RecyclableMaterialEntity
import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.MaterialCondition
import com.econect.app.domain.model.MaterialQuantity
import com.econect.app.domain.model.MaterialStatus
import com.econect.app.domain.model.MaterialType
import com.econect.app.domain.model.MaterialUnit
import com.econect.app.domain.model.RecyclableMaterial

fun RecyclableMaterialEntity.toDomain(): RecyclableMaterial = RecyclableMaterial(
    id = id,
    citizenId = citizenId,
    type = MaterialType.valueOf(type),
    condition = MaterialCondition.valueOf(condition),
    quantity = MaterialQuantity(
        value = quantityValue,
        unit = MaterialUnit.valueOf(quantityUnit)
    ),
    pickupLocation = LatLng(latitude = pickupLat, longitude = pickupLng),
    status = MaterialStatus.valueOf(status),
    createdAt = createdAt
)

fun RecyclableMaterial.toEntity(): RecyclableMaterialEntity = RecyclableMaterialEntity(
    id = id,
    citizenId = citizenId,
    type = type.name,
    condition = condition.name,
    quantityValue = quantity.value,
    quantityUnit = quantity.unit.name,
    pickupLat = pickupLocation.latitude,
    pickupLng = pickupLocation.longitude,
    status = status.name,
    createdAt = createdAt
)
