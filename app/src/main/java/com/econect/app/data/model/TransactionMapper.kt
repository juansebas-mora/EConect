package com.econect.app.data.model

import com.econect.app.data.local.db.entity.TransactionEntity
import com.econect.app.domain.model.MaterialQuantity
import com.econect.app.domain.model.MaterialUnit
import com.econect.app.domain.model.Transaction

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    routeId = routeId,
    recyclerId = recyclerId,
    citizenId = citizenId,
    materialId = materialId,
    confirmedQuantity = MaterialQuantity(
        value = confirmedQuantityValue,
        unit = MaterialUnit.valueOf(confirmedQuantityUnit)
    ),
    pricePerUnit = pricePerUnit,
    totalAmount = totalAmount,
    recyclingCenterId = recyclingCenterId,
    completedAt = completedAt
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    routeId = routeId,
    recyclerId = recyclerId,
    citizenId = citizenId,
    materialId = materialId,
    confirmedQuantityValue = confirmedQuantity.value,
    confirmedQuantityUnit = confirmedQuantity.unit.name,
    pricePerUnit = pricePerUnit,
    totalAmount = totalAmount,
    recyclingCenterId = recyclingCenterId,
    completedAt = completedAt
)
