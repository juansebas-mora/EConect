package com.econect.app.`data`.local.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.econect.app.`data`.local.db.entity.TransactionEntity
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
public class TransactionDao_Impl(
  __db: RoomDatabase,
) : TransactionDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTransactionEntity: EntityInsertAdapter<TransactionEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfTransactionEntity = object : EntityInsertAdapter<TransactionEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `transactions` (`id`,`routeId`,`recyclerId`,`citizenId`,`materialId`,`confirmedQuantityValue`,`confirmedQuantityUnit`,`pricePerUnit`,`totalAmount`,`recyclingCenterId`,`completedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TransactionEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.routeId)
        statement.bindText(3, entity.recyclerId)
        statement.bindText(4, entity.citizenId)
        statement.bindText(5, entity.materialId)
        statement.bindDouble(6, entity.confirmedQuantityValue)
        statement.bindText(7, entity.confirmedQuantityUnit)
        statement.bindDouble(8, entity.pricePerUnit)
        statement.bindDouble(9, entity.totalAmount)
        statement.bindText(10, entity.recyclingCenterId)
        statement.bindLong(11, entity.completedAt)
      }
    }
  }

  public override suspend fun insert(transaction: TransactionEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfTransactionEntity.insert(_connection, transaction)
  }

  public override suspend fun insertAll(transactions: List<TransactionEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTransactionEntity.insert(_connection, transactions)
  }

  public override suspend fun getById(id: String): TransactionEntity? {
    val _sql: String = "SELECT * FROM transactions WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRouteId: Int = getColumnIndexOrThrow(_stmt, "routeId")
        val _columnIndexOfRecyclerId: Int = getColumnIndexOrThrow(_stmt, "recyclerId")
        val _columnIndexOfCitizenId: Int = getColumnIndexOrThrow(_stmt, "citizenId")
        val _columnIndexOfMaterialId: Int = getColumnIndexOrThrow(_stmt, "materialId")
        val _columnIndexOfConfirmedQuantityValue: Int = getColumnIndexOrThrow(_stmt,
            "confirmedQuantityValue")
        val _columnIndexOfConfirmedQuantityUnit: Int = getColumnIndexOrThrow(_stmt,
            "confirmedQuantityUnit")
        val _columnIndexOfPricePerUnit: Int = getColumnIndexOrThrow(_stmt, "pricePerUnit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfRecyclingCenterId: Int = getColumnIndexOrThrow(_stmt, "recyclingCenterId")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _result: TransactionEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpRouteId: String
          _tmpRouteId = _stmt.getText(_columnIndexOfRouteId)
          val _tmpRecyclerId: String
          _tmpRecyclerId = _stmt.getText(_columnIndexOfRecyclerId)
          val _tmpCitizenId: String
          _tmpCitizenId = _stmt.getText(_columnIndexOfCitizenId)
          val _tmpMaterialId: String
          _tmpMaterialId = _stmt.getText(_columnIndexOfMaterialId)
          val _tmpConfirmedQuantityValue: Double
          _tmpConfirmedQuantityValue = _stmt.getDouble(_columnIndexOfConfirmedQuantityValue)
          val _tmpConfirmedQuantityUnit: String
          _tmpConfirmedQuantityUnit = _stmt.getText(_columnIndexOfConfirmedQuantityUnit)
          val _tmpPricePerUnit: Double
          _tmpPricePerUnit = _stmt.getDouble(_columnIndexOfPricePerUnit)
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpRecyclingCenterId: String
          _tmpRecyclingCenterId = _stmt.getText(_columnIndexOfRecyclingCenterId)
          val _tmpCompletedAt: Long
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          _result =
              TransactionEntity(_tmpId,_tmpRouteId,_tmpRecyclerId,_tmpCitizenId,_tmpMaterialId,_tmpConfirmedQuantityValue,_tmpConfirmedQuantityUnit,_tmpPricePerUnit,_tmpTotalAmount,_tmpRecyclingCenterId,_tmpCompletedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByRecycler(recyclerId: String): Flow<List<TransactionEntity>> {
    val _sql: String = "SELECT * FROM transactions WHERE recyclerId = ? ORDER BY completedAt DESC"
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, recyclerId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRouteId: Int = getColumnIndexOrThrow(_stmt, "routeId")
        val _columnIndexOfRecyclerId: Int = getColumnIndexOrThrow(_stmt, "recyclerId")
        val _columnIndexOfCitizenId: Int = getColumnIndexOrThrow(_stmt, "citizenId")
        val _columnIndexOfMaterialId: Int = getColumnIndexOrThrow(_stmt, "materialId")
        val _columnIndexOfConfirmedQuantityValue: Int = getColumnIndexOrThrow(_stmt,
            "confirmedQuantityValue")
        val _columnIndexOfConfirmedQuantityUnit: Int = getColumnIndexOrThrow(_stmt,
            "confirmedQuantityUnit")
        val _columnIndexOfPricePerUnit: Int = getColumnIndexOrThrow(_stmt, "pricePerUnit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfRecyclingCenterId: Int = getColumnIndexOrThrow(_stmt, "recyclingCenterId")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _result: MutableList<TransactionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpRouteId: String
          _tmpRouteId = _stmt.getText(_columnIndexOfRouteId)
          val _tmpRecyclerId: String
          _tmpRecyclerId = _stmt.getText(_columnIndexOfRecyclerId)
          val _tmpCitizenId: String
          _tmpCitizenId = _stmt.getText(_columnIndexOfCitizenId)
          val _tmpMaterialId: String
          _tmpMaterialId = _stmt.getText(_columnIndexOfMaterialId)
          val _tmpConfirmedQuantityValue: Double
          _tmpConfirmedQuantityValue = _stmt.getDouble(_columnIndexOfConfirmedQuantityValue)
          val _tmpConfirmedQuantityUnit: String
          _tmpConfirmedQuantityUnit = _stmt.getText(_columnIndexOfConfirmedQuantityUnit)
          val _tmpPricePerUnit: Double
          _tmpPricePerUnit = _stmt.getDouble(_columnIndexOfPricePerUnit)
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpRecyclingCenterId: String
          _tmpRecyclingCenterId = _stmt.getText(_columnIndexOfRecyclingCenterId)
          val _tmpCompletedAt: Long
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          _item =
              TransactionEntity(_tmpId,_tmpRouteId,_tmpRecyclerId,_tmpCitizenId,_tmpMaterialId,_tmpConfirmedQuantityValue,_tmpConfirmedQuantityUnit,_tmpPricePerUnit,_tmpTotalAmount,_tmpRecyclingCenterId,_tmpCompletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByCitizen(citizenId: String): Flow<List<TransactionEntity>> {
    val _sql: String = "SELECT * FROM transactions WHERE citizenId = ? ORDER BY completedAt DESC"
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, citizenId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRouteId: Int = getColumnIndexOrThrow(_stmt, "routeId")
        val _columnIndexOfRecyclerId: Int = getColumnIndexOrThrow(_stmt, "recyclerId")
        val _columnIndexOfCitizenId: Int = getColumnIndexOrThrow(_stmt, "citizenId")
        val _columnIndexOfMaterialId: Int = getColumnIndexOrThrow(_stmt, "materialId")
        val _columnIndexOfConfirmedQuantityValue: Int = getColumnIndexOrThrow(_stmt,
            "confirmedQuantityValue")
        val _columnIndexOfConfirmedQuantityUnit: Int = getColumnIndexOrThrow(_stmt,
            "confirmedQuantityUnit")
        val _columnIndexOfPricePerUnit: Int = getColumnIndexOrThrow(_stmt, "pricePerUnit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfRecyclingCenterId: Int = getColumnIndexOrThrow(_stmt, "recyclingCenterId")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _result: MutableList<TransactionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpRouteId: String
          _tmpRouteId = _stmt.getText(_columnIndexOfRouteId)
          val _tmpRecyclerId: String
          _tmpRecyclerId = _stmt.getText(_columnIndexOfRecyclerId)
          val _tmpCitizenId: String
          _tmpCitizenId = _stmt.getText(_columnIndexOfCitizenId)
          val _tmpMaterialId: String
          _tmpMaterialId = _stmt.getText(_columnIndexOfMaterialId)
          val _tmpConfirmedQuantityValue: Double
          _tmpConfirmedQuantityValue = _stmt.getDouble(_columnIndexOfConfirmedQuantityValue)
          val _tmpConfirmedQuantityUnit: String
          _tmpConfirmedQuantityUnit = _stmt.getText(_columnIndexOfConfirmedQuantityUnit)
          val _tmpPricePerUnit: Double
          _tmpPricePerUnit = _stmt.getDouble(_columnIndexOfPricePerUnit)
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpRecyclingCenterId: String
          _tmpRecyclingCenterId = _stmt.getText(_columnIndexOfRecyclingCenterId)
          val _tmpCompletedAt: Long
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          _item =
              TransactionEntity(_tmpId,_tmpRouteId,_tmpRecyclerId,_tmpCitizenId,_tmpMaterialId,_tmpConfirmedQuantityValue,_tmpConfirmedQuantityUnit,_tmpPricePerUnit,_tmpTotalAmount,_tmpRecyclingCenterId,_tmpCompletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByRoute(routeId: String): Flow<List<TransactionEntity>> {
    val _sql: String = "SELECT * FROM transactions WHERE routeId = ?"
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, routeId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRouteId: Int = getColumnIndexOrThrow(_stmt, "routeId")
        val _columnIndexOfRecyclerId: Int = getColumnIndexOrThrow(_stmt, "recyclerId")
        val _columnIndexOfCitizenId: Int = getColumnIndexOrThrow(_stmt, "citizenId")
        val _columnIndexOfMaterialId: Int = getColumnIndexOrThrow(_stmt, "materialId")
        val _columnIndexOfConfirmedQuantityValue: Int = getColumnIndexOrThrow(_stmt,
            "confirmedQuantityValue")
        val _columnIndexOfConfirmedQuantityUnit: Int = getColumnIndexOrThrow(_stmt,
            "confirmedQuantityUnit")
        val _columnIndexOfPricePerUnit: Int = getColumnIndexOrThrow(_stmt, "pricePerUnit")
        val _columnIndexOfTotalAmount: Int = getColumnIndexOrThrow(_stmt, "totalAmount")
        val _columnIndexOfRecyclingCenterId: Int = getColumnIndexOrThrow(_stmt, "recyclingCenterId")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _result: MutableList<TransactionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpRouteId: String
          _tmpRouteId = _stmt.getText(_columnIndexOfRouteId)
          val _tmpRecyclerId: String
          _tmpRecyclerId = _stmt.getText(_columnIndexOfRecyclerId)
          val _tmpCitizenId: String
          _tmpCitizenId = _stmt.getText(_columnIndexOfCitizenId)
          val _tmpMaterialId: String
          _tmpMaterialId = _stmt.getText(_columnIndexOfMaterialId)
          val _tmpConfirmedQuantityValue: Double
          _tmpConfirmedQuantityValue = _stmt.getDouble(_columnIndexOfConfirmedQuantityValue)
          val _tmpConfirmedQuantityUnit: String
          _tmpConfirmedQuantityUnit = _stmt.getText(_columnIndexOfConfirmedQuantityUnit)
          val _tmpPricePerUnit: Double
          _tmpPricePerUnit = _stmt.getDouble(_columnIndexOfPricePerUnit)
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount)
          val _tmpRecyclingCenterId: String
          _tmpRecyclingCenterId = _stmt.getText(_columnIndexOfRecyclingCenterId)
          val _tmpCompletedAt: Long
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          _item =
              TransactionEntity(_tmpId,_tmpRouteId,_tmpRecyclerId,_tmpCitizenId,_tmpMaterialId,_tmpConfirmedQuantityValue,_tmpConfirmedQuantityUnit,_tmpPricePerUnit,_tmpTotalAmount,_tmpRecyclingCenterId,_tmpCompletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: String) {
    val _sql: String = "DELETE FROM transactions WHERE id = ?"
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
    val _sql: String = "DELETE FROM transactions"
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
