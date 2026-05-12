package com.econect.app.`data`.local.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.econect.app.`data`.local.db.entity.UserEntity
import javax.`annotation`.processing.Generated
import kotlin.Float
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
public class UserDao_Impl(
  __db: RoomDatabase,
) : UserDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfUserEntity: EntityInsertAdapter<UserEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfUserEntity = object : EntityInsertAdapter<UserEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `users` (`id`,`name`,`email`,`phone`,`userType`,`preferredLocationsJson`,`schedulesJson`,`rating`,`ratingCount`,`preferredRecyclingCenterId`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UserEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.email)
        statement.bindText(4, entity.phone)
        statement.bindText(5, entity.userType)
        statement.bindText(6, entity.preferredLocationsJson)
        statement.bindText(7, entity.schedulesJson)
        statement.bindDouble(8, entity.rating.toDouble())
        statement.bindLong(9, entity.ratingCount.toLong())
        val _tmpPreferredRecyclingCenterId: String? = entity.preferredRecyclingCenterId
        if (_tmpPreferredRecyclingCenterId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpPreferredRecyclingCenterId)
        }
      }
    }
  }

  public override suspend fun insert(user: UserEntity): Unit = performSuspending(__db, false, true)
      { _connection ->
    __insertAdapterOfUserEntity.insert(_connection, user)
  }

  public override suspend fun insertAll(users: List<UserEntity>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfUserEntity.insert(_connection, users)
  }

  public override suspend fun getById(id: String): UserEntity? {
    val _sql: String = "SELECT * FROM users WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfUserType: Int = getColumnIndexOrThrow(_stmt, "userType")
        val _columnIndexOfPreferredLocationsJson: Int = getColumnIndexOrThrow(_stmt,
            "preferredLocationsJson")
        val _columnIndexOfSchedulesJson: Int = getColumnIndexOrThrow(_stmt, "schedulesJson")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfRatingCount: Int = getColumnIndexOrThrow(_stmt, "ratingCount")
        val _columnIndexOfPreferredRecyclingCenterId: Int = getColumnIndexOrThrow(_stmt,
            "preferredRecyclingCenterId")
        val _result: UserEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpUserType: String
          _tmpUserType = _stmt.getText(_columnIndexOfUserType)
          val _tmpPreferredLocationsJson: String
          _tmpPreferredLocationsJson = _stmt.getText(_columnIndexOfPreferredLocationsJson)
          val _tmpSchedulesJson: String
          _tmpSchedulesJson = _stmt.getText(_columnIndexOfSchedulesJson)
          val _tmpRating: Float
          _tmpRating = _stmt.getDouble(_columnIndexOfRating).toFloat()
          val _tmpRatingCount: Int
          _tmpRatingCount = _stmt.getLong(_columnIndexOfRatingCount).toInt()
          val _tmpPreferredRecyclingCenterId: String?
          if (_stmt.isNull(_columnIndexOfPreferredRecyclingCenterId)) {
            _tmpPreferredRecyclingCenterId = null
          } else {
            _tmpPreferredRecyclingCenterId = _stmt.getText(_columnIndexOfPreferredRecyclingCenterId)
          }
          _result =
              UserEntity(_tmpId,_tmpName,_tmpEmail,_tmpPhone,_tmpUserType,_tmpPreferredLocationsJson,_tmpSchedulesJson,_tmpRating,_tmpRatingCount,_tmpPreferredRecyclingCenterId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeById(id: String): Flow<UserEntity?> {
    val _sql: String = "SELECT * FROM users WHERE id = ?"
    return createFlow(__db, false, arrayOf("users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfUserType: Int = getColumnIndexOrThrow(_stmt, "userType")
        val _columnIndexOfPreferredLocationsJson: Int = getColumnIndexOrThrow(_stmt,
            "preferredLocationsJson")
        val _columnIndexOfSchedulesJson: Int = getColumnIndexOrThrow(_stmt, "schedulesJson")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfRatingCount: Int = getColumnIndexOrThrow(_stmt, "ratingCount")
        val _columnIndexOfPreferredRecyclingCenterId: Int = getColumnIndexOrThrow(_stmt,
            "preferredRecyclingCenterId")
        val _result: UserEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpUserType: String
          _tmpUserType = _stmt.getText(_columnIndexOfUserType)
          val _tmpPreferredLocationsJson: String
          _tmpPreferredLocationsJson = _stmt.getText(_columnIndexOfPreferredLocationsJson)
          val _tmpSchedulesJson: String
          _tmpSchedulesJson = _stmt.getText(_columnIndexOfSchedulesJson)
          val _tmpRating: Float
          _tmpRating = _stmt.getDouble(_columnIndexOfRating).toFloat()
          val _tmpRatingCount: Int
          _tmpRatingCount = _stmt.getLong(_columnIndexOfRatingCount).toInt()
          val _tmpPreferredRecyclingCenterId: String?
          if (_stmt.isNull(_columnIndexOfPreferredRecyclingCenterId)) {
            _tmpPreferredRecyclingCenterId = null
          } else {
            _tmpPreferredRecyclingCenterId = _stmt.getText(_columnIndexOfPreferredRecyclingCenterId)
          }
          _result =
              UserEntity(_tmpId,_tmpName,_tmpEmail,_tmpPhone,_tmpUserType,_tmpPreferredLocationsJson,_tmpSchedulesJson,_tmpRating,_tmpRatingCount,_tmpPreferredRecyclingCenterId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByType(type: String): Flow<List<UserEntity>> {
    val _sql: String = "SELECT * FROM users WHERE userType = ?"
    return createFlow(__db, false, arrayOf("users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfUserType: Int = getColumnIndexOrThrow(_stmt, "userType")
        val _columnIndexOfPreferredLocationsJson: Int = getColumnIndexOrThrow(_stmt,
            "preferredLocationsJson")
        val _columnIndexOfSchedulesJson: Int = getColumnIndexOrThrow(_stmt, "schedulesJson")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfRatingCount: Int = getColumnIndexOrThrow(_stmt, "ratingCount")
        val _columnIndexOfPreferredRecyclingCenterId: Int = getColumnIndexOrThrow(_stmt,
            "preferredRecyclingCenterId")
        val _result: MutableList<UserEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UserEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpUserType: String
          _tmpUserType = _stmt.getText(_columnIndexOfUserType)
          val _tmpPreferredLocationsJson: String
          _tmpPreferredLocationsJson = _stmt.getText(_columnIndexOfPreferredLocationsJson)
          val _tmpSchedulesJson: String
          _tmpSchedulesJson = _stmt.getText(_columnIndexOfSchedulesJson)
          val _tmpRating: Float
          _tmpRating = _stmt.getDouble(_columnIndexOfRating).toFloat()
          val _tmpRatingCount: Int
          _tmpRatingCount = _stmt.getLong(_columnIndexOfRatingCount).toInt()
          val _tmpPreferredRecyclingCenterId: String?
          if (_stmt.isNull(_columnIndexOfPreferredRecyclingCenterId)) {
            _tmpPreferredRecyclingCenterId = null
          } else {
            _tmpPreferredRecyclingCenterId = _stmt.getText(_columnIndexOfPreferredRecyclingCenterId)
          }
          _item =
              UserEntity(_tmpId,_tmpName,_tmpEmail,_tmpPhone,_tmpUserType,_tmpPreferredLocationsJson,_tmpSchedulesJson,_tmpRating,_tmpRatingCount,_tmpPreferredRecyclingCenterId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: String) {
    val _sql: String = "DELETE FROM users WHERE id = ?"
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
    val _sql: String = "DELETE FROM users"
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
