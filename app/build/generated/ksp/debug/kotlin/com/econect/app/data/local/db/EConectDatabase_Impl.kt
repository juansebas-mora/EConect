package com.econect.app.`data`.local.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.econect.app.`data`.local.db.dao.RecyclableMaterialDao
import com.econect.app.`data`.local.db.dao.RecyclableMaterialDao_Impl
import com.econect.app.`data`.local.db.dao.RouteDao
import com.econect.app.`data`.local.db.dao.RouteDao_Impl
import com.econect.app.`data`.local.db.dao.TransactionDao
import com.econect.app.`data`.local.db.dao.TransactionDao_Impl
import com.econect.app.`data`.local.db.dao.UserDao
import com.econect.app.`data`.local.db.dao.UserDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class EConectDatabase_Impl : EConectDatabase() {
  private val _userDao: Lazy<UserDao> = lazy {
    UserDao_Impl(this)
  }

  private val _recyclableMaterialDao: Lazy<RecyclableMaterialDao> = lazy {
    RecyclableMaterialDao_Impl(this)
  }

  private val _routeDao: Lazy<RouteDao> = lazy {
    RouteDao_Impl(this)
  }

  private val _transactionDao: Lazy<TransactionDao> = lazy {
    TransactionDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2,
        "e6952789ea93265c320d9199b45d904d", "851f2586f09dc516f57e40c39996b63e") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `email` TEXT NOT NULL, `phone` TEXT NOT NULL, `userType` TEXT NOT NULL, `preferredLocationsJson` TEXT NOT NULL, `schedulesJson` TEXT NOT NULL, `rating` REAL NOT NULL, `ratingCount` INTEGER NOT NULL, `preferredRecyclingCenterId` TEXT, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `recyclable_materials` (`id` TEXT NOT NULL, `citizenId` TEXT NOT NULL, `type` TEXT NOT NULL, `condition` TEXT NOT NULL, `quantityValue` REAL NOT NULL, `quantityUnit` TEXT NOT NULL, `pickupLat` REAL NOT NULL, `pickupLng` REAL NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `routes` (`id` TEXT NOT NULL, `recyclerId` TEXT NOT NULL, `date` TEXT NOT NULL, `status` TEXT NOT NULL, `stopsJson` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`id` TEXT NOT NULL, `routeId` TEXT NOT NULL, `recyclerId` TEXT NOT NULL, `citizenId` TEXT NOT NULL, `materialId` TEXT NOT NULL, `confirmedQuantityValue` REAL NOT NULL, `confirmedQuantityUnit` TEXT NOT NULL, `pricePerUnit` REAL NOT NULL, `totalAmount` REAL NOT NULL, `recyclingCenterId` TEXT NOT NULL, `completedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e6952789ea93265c320d9199b45d904d')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `users`")
        connection.execSQL("DROP TABLE IF EXISTS `recyclable_materials`")
        connection.execSQL("DROP TABLE IF EXISTS `routes`")
        connection.execSQL("DROP TABLE IF EXISTS `transactions`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsUsers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUsers.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("email", TableInfo.Column("email", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("phone", TableInfo.Column("phone", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("userType", TableInfo.Column("userType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("preferredLocationsJson", TableInfo.Column("preferredLocationsJson",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("schedulesJson", TableInfo.Column("schedulesJson", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("rating", TableInfo.Column("rating", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("ratingCount", TableInfo.Column("ratingCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("preferredRecyclingCenterId",
            TableInfo.Column("preferredRecyclingCenterId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUsers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUsers: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUsers: TableInfo = TableInfo("users", _columnsUsers, _foreignKeysUsers,
            _indicesUsers)
        val _existingUsers: TableInfo = read(connection, "users")
        if (!_infoUsers.equals(_existingUsers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |users(com.econect.app.data.local.db.entity.UserEntity).
              | Expected:
              |""".trimMargin() + _infoUsers + """
              |
              | Found:
              |""".trimMargin() + _existingUsers)
        }
        val _columnsRecyclableMaterials: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRecyclableMaterials.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRecyclableMaterials.put("citizenId", TableInfo.Column("citizenId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecyclableMaterials.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRecyclableMaterials.put("condition", TableInfo.Column("condition", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecyclableMaterials.put("quantityValue", TableInfo.Column("quantityValue", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecyclableMaterials.put("quantityUnit", TableInfo.Column("quantityUnit", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecyclableMaterials.put("pickupLat", TableInfo.Column("pickupLat", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecyclableMaterials.put("pickupLng", TableInfo.Column("pickupLng", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecyclableMaterials.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRecyclableMaterials.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRecyclableMaterials: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRecyclableMaterials: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoRecyclableMaterials: TableInfo = TableInfo("recyclable_materials",
            _columnsRecyclableMaterials, _foreignKeysRecyclableMaterials,
            _indicesRecyclableMaterials)
        val _existingRecyclableMaterials: TableInfo = read(connection, "recyclable_materials")
        if (!_infoRecyclableMaterials.equals(_existingRecyclableMaterials)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |recyclable_materials(com.econect.app.data.local.db.entity.RecyclableMaterialEntity).
              | Expected:
              |""".trimMargin() + _infoRecyclableMaterials + """
              |
              | Found:
              |""".trimMargin() + _existingRecyclableMaterials)
        }
        val _columnsRoutes: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRoutes.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRoutes.put("recyclerId", TableInfo.Column("recyclerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRoutes.put("date", TableInfo.Column("date", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRoutes.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRoutes.put("stopsJson", TableInfo.Column("stopsJson", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRoutes: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRoutes: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoRoutes: TableInfo = TableInfo("routes", _columnsRoutes, _foreignKeysRoutes,
            _indicesRoutes)
        val _existingRoutes: TableInfo = read(connection, "routes")
        if (!_infoRoutes.equals(_existingRoutes)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |routes(com.econect.app.data.local.db.entity.RouteEntity).
              | Expected:
              |""".trimMargin() + _infoRoutes + """
              |
              | Found:
              |""".trimMargin() + _existingRoutes)
        }
        val _columnsTransactions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTransactions.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("routeId", TableInfo.Column("routeId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("recyclerId", TableInfo.Column("recyclerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("citizenId", TableInfo.Column("citizenId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("materialId", TableInfo.Column("materialId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("confirmedQuantityValue",
            TableInfo.Column("confirmedQuantityValue", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("confirmedQuantityUnit", TableInfo.Column("confirmedQuantityUnit",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("pricePerUnit", TableInfo.Column("pricePerUnit", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("totalAmount", TableInfo.Column("totalAmount", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("recyclingCenterId", TableInfo.Column("recyclingCenterId", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("completedAt", TableInfo.Column("completedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTransactions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTransactions: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoTransactions: TableInfo = TableInfo("transactions", _columnsTransactions,
            _foreignKeysTransactions, _indicesTransactions)
        val _existingTransactions: TableInfo = read(connection, "transactions")
        if (!_infoTransactions.equals(_existingTransactions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |transactions(com.econect.app.data.local.db.entity.TransactionEntity).
              | Expected:
              |""".trimMargin() + _infoTransactions + """
              |
              | Found:
              |""".trimMargin() + _existingTransactions)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "users", "recyclable_materials",
        "routes", "transactions")
  }

  public override fun clearAllTables() {
    super.performClear(false, "users", "recyclable_materials", "routes", "transactions")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(UserDao::class, UserDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(RecyclableMaterialDao::class,
        RecyclableMaterialDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(RouteDao::class, RouteDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TransactionDao::class, TransactionDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun userDao(): UserDao = _userDao.value

  public override fun recyclableMaterialDao(): RecyclableMaterialDao = _recyclableMaterialDao.value

  public override fun routeDao(): RouteDao = _routeDao.value

  public override fun transactionDao(): TransactionDao = _transactionDao.value
}
