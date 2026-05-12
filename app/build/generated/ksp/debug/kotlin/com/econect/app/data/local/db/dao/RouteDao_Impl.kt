package com.econect.app.`data`.local.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.econect.app.`data`.local.db.entity.RouteEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
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
public class RouteDao_Impl(
  __db: RoomDatabase,
) : RouteDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfRouteEntity: EntityInsertAdapter<RouteEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfRouteEntity = object : EntityInsertAdapter<RouteEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `routes` (`id`,`recyclerId`,`date`,`status`,`stopsJson`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: RouteEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.recyclerId)
        statement.bindText(3, entity.date)
        statement.bindText(4, entity.status)
        statement.bindText(5, entity.stopsJson)
      }
    }
  }

  public override suspend fun insert(route: RouteEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfRouteEntity.insert(_connection, route)
  }

  public override suspend fun insertAll(routes: List<RouteEntity>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfRouteEntity.insert(_connection, routes)
  }

  public override suspend fun getById(id: String): RouteEntity? {
    val _sql: String = "SELECT * FROM routes WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRecyclerId: Int = getColumnIndexOrThrow(_stmt, "recyclerId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfStopsJson: Int = getColumnIndexOrThrow(_stmt, "stopsJson")
        val _result: RouteEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpRecyclerId: String
          _tmpRecyclerId = _stmt.getText(_columnIndexOfRecyclerId)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpStopsJson: String
          _tmpStopsJson = _stmt.getText(_columnIndexOfStopsJson)
          _result = RouteEntity(_tmpId,_tmpRecyclerId,_tmpDate,_tmpStatus,_tmpStopsJson)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByRecycler(recyclerId: String): Flow<List<RouteEntity>> {
    val _sql: String = "SELECT * FROM routes WHERE recyclerId = ?"
    return createFlow(__db, false, arrayOf("routes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, recyclerId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRecyclerId: Int = getColumnIndexOrThrow(_stmt, "recyclerId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfStopsJson: Int = getColumnIndexOrThrow(_stmt, "stopsJson")
        val _result: MutableList<RouteEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RouteEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpRecyclerId: String
          _tmpRecyclerId = _stmt.getText(_columnIndexOfRecyclerId)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpStopsJson: String
          _tmpStopsJson = _stmt.getText(_columnIndexOfStopsJson)
          _item = RouteEntity(_tmpId,_tmpRecyclerId,_tmpDate,_tmpStatus,_tmpStopsJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByRecyclerAndDate(recyclerId: String, date: String):
      Flow<List<RouteEntity>> {
    val _sql: String = "SELECT * FROM routes WHERE recyclerId = ? AND date = ?"
    return createFlow(__db, false, arrayOf("routes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, recyclerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, date)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRecyclerId: Int = getColumnIndexOrThrow(_stmt, "recyclerId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfStopsJson: Int = getColumnIndexOrThrow(_stmt, "stopsJson")
        val _result: MutableList<RouteEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RouteEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpRecyclerId: String
          _tmpRecyclerId = _stmt.getText(_columnIndexOfRecyclerId)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpStopsJson: String
          _tmpStopsJson = _stmt.getText(_columnIndexOfStopsJson)
          _item = RouteEntity(_tmpId,_tmpRecyclerId,_tmpDate,_tmpStatus,_tmpStopsJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByStatus(status: String): Flow<List<RouteEntity>> {
    val _sql: String = "SELECT * FROM routes WHERE status = ?"
    return createFlow(__db, false, arrayOf("routes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRecyclerId: Int = getColumnIndexOrThrow(_stmt, "recyclerId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfStopsJson: Int = getColumnIndexOrThrow(_stmt, "stopsJson")
        val _result: MutableList<RouteEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RouteEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpRecyclerId: String
          _tmpRecyclerId = _stmt.getText(_columnIndexOfRecyclerId)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpStopsJson: String
          _tmpStopsJson = _stmt.getText(_columnIndexOfStopsJson)
          _item = RouteEntity(_tmpId,_tmpRecyclerId,_tmpDate,_tmpStatus,_tmpStopsJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: String) {
    val _sql: String = "DELETE FROM routes WHERE id = ?"
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
    val _sql: String = "DELETE FROM routes"
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
