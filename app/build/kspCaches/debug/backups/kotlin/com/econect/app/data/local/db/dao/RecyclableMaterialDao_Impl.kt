package com.econect.app.`data`.local.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.econect.app.`data`.local.db.entity.RecyclableMaterialEntity
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class RecyclableMaterialDao_Impl(
  __db: RoomDatabase,
) : RecyclableMaterialDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfRecyclableMaterialEntity:
      EntityInsertAdapter<RecyclableMaterialEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfRecyclableMaterialEntity = object :
        EntityInsertAdapter<RecyclableMaterialEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `recyclable_materials` (`id`,`citizenId`,`type`,`condition`,`quantityValue`,`quantityUnit`,`pickupLat`,`pickupLng`,`status`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: RecyclableMaterialEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.citizenId)
        statement.bindText(3, entity.type)
        statement.bindText(4, entity.condition)
        statement.bindDouble(5, entity.quantityValue)
        statement.bindText(6, entity.quantityUnit)
        statement.bindDouble(7, entity.pickupLat)
        statement.bindDouble(8, entity.pickupLng)
        statement.bindText(9, entity.status)
        statement.bindLong(10, entity.createdAt)
      }
    }
  }

  public override suspend fun insert(material: RecyclableMaterialEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfRecyclableMaterialEntity.insert(_connection, material)
  }

  public override suspend fun insertAll(materials: List<RecyclableMaterialEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfRecyclableMaterialEntity.insert(_connection, materials)
  }

  public override suspend fun getById(id: String): RecyclableMaterialEntity? {
    val _sql: String = "SELECT * FROM recyclable_materials WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCitizenId: Int = getColumnIndexOrThrow(_stmt, "citizenId")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfQuantityValue: Int = getColumnIndexOrThrow(_stmt, "quantityValue")
        val _columnIndexOfQuantityUnit: Int = getColumnIndexOrThrow(_stmt, "quantityUnit")
        val _columnIndexOfPickupLat: Int = getColumnIndexOrThrow(_stmt, "pickupLat")
        val _columnIndexOfPickupLng: Int = getColumnIndexOrThrow(_stmt, "pickupLng")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: RecyclableMaterialEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpCitizenId: String
          _tmpCitizenId = _stmt.getText(_columnIndexOfCitizenId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpCondition: String
          _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          val _tmpQuantityValue: Double
          _tmpQuantityValue = _stmt.getDouble(_columnIndexOfQuantityValue)
          val _tmpQuantityUnit: String
          _tmpQuantityUnit = _stmt.getText(_columnIndexOfQuantityUnit)
          val _tmpPickupLat: Double
          _tmpPickupLat = _stmt.getDouble(_columnIndexOfPickupLat)
          val _tmpPickupLng: Double
          _tmpPickupLng = _stmt.getDouble(_columnIndexOfPickupLng)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              RecyclableMaterialEntity(_tmpId,_tmpCitizenId,_tmpType,_tmpCondition,_tmpQuantityValue,_tmpQuantityUnit,_tmpPickupLat,_tmpPickupLng,_tmpStatus,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByCitizen(citizenId: String): Flow<List<RecyclableMaterialEntity>> {
    val _sql: String = "SELECT * FROM recyclable_materials WHERE citizenId = ?"
    return createFlow(__db, false, arrayOf("recyclable_materials")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, citizenId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCitizenId: Int = getColumnIndexOrThrow(_stmt, "citizenId")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfQuantityValue: Int = getColumnIndexOrThrow(_stmt, "quantityValue")
        val _columnIndexOfQuantityUnit: Int = getColumnIndexOrThrow(_stmt, "quantityUnit")
        val _columnIndexOfPickupLat: Int = getColumnIndexOrThrow(_stmt, "pickupLat")
        val _columnIndexOfPickupLng: Int = getColumnIndexOrThrow(_stmt, "pickupLng")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<RecyclableMaterialEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RecyclableMaterialEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpCitizenId: String
          _tmpCitizenId = _stmt.getText(_columnIndexOfCitizenId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpCondition: String
          _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          val _tmpQuantityValue: Double
          _tmpQuantityValue = _stmt.getDouble(_columnIndexOfQuantityValue)
          val _tmpQuantityUnit: String
          _tmpQuantityUnit = _stmt.getText(_columnIndexOfQuantityUnit)
          val _tmpPickupLat: Double
          _tmpPickupLat = _stmt.getDouble(_columnIndexOfPickupLat)
          val _tmpPickupLng: Double
          _tmpPickupLng = _stmt.getDouble(_columnIndexOfPickupLng)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              RecyclableMaterialEntity(_tmpId,_tmpCitizenId,_tmpType,_tmpCondition,_tmpQuantityValue,_tmpQuantityUnit,_tmpPickupLat,_tmpPickupLng,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByStatus(status: String): Flow<List<RecyclableMaterialEntity>> {
    val _sql: String = "SELECT * FROM recyclable_materials WHERE status = ?"
    return createFlow(__db, false, arrayOf("recyclable_materials")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCitizenId: Int = getColumnIndexOrThrow(_stmt, "citizenId")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfCondition: Int = getColumnIndexOrThrow(_stmt, "condition")
        val _columnIndexOfQuantityValue: Int = getColumnIndexOrThrow(_stmt, "quantityValue")
        val _columnIndexOfQuantityUnit: Int = getColumnIndexOrThrow(_stmt, "quantityUnit")
        val _columnIndexOfPickupLat: Int = getColumnIndexOrThrow(_stmt, "pickupLat")
        val _columnIndexOfPickupLng: Int = getColumnIndexOrThrow(_stmt, "pickupLng")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<RecyclableMaterialEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RecyclableMaterialEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpCitizenId: String
          _tmpCitizenId = _stmt.getText(_columnIndexOfCitizenId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpCondition: String
          _tmpCondition = _stmt.getText(_columnIndexOfCondition)
          val _tmpQuantityValue: Double
          _tmpQuantityValue = _stmt.getDouble(_columnIndexOfQuantityValue)
          val _tmpQuantityUnit: String
          _tmpQuantityUnit = _stmt.getText(_columnIndexOfQuantityUnit)
          val _tmpPickupLat: Double
          _tmpPickupLat = _stmt.getDouble(_columnIndexOfPickupLat)
          val _tmpPickupLng: Double
          _tmpPickupLng = _stmt.getDouble(_columnIndexOfPickupLng)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              RecyclableMaterialEntity(_tmpId,_tmpCitizenId,_tmpType,_tmpCondition,_tmpQuantityValue,_tmpQuantityUnit,_tmpPickupLat,_tmpPickupLng,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: String) {
    val _sql: String = "DELETE FROM recyclable_materials WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM recyclable_materials"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
