package com.econect.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.econect.app.data.local.db.dao.RecyclableMaterialDao
import com.econect.app.data.local.db.dao.RouteDao
import com.econect.app.data.local.db.dao.TransactionDao
import com.econect.app.data.local.db.dao.UserDao
import com.econect.app.data.local.db.entity.RecyclableMaterialEntity
import com.econect.app.data.local.db.entity.RouteEntity
import com.econect.app.data.local.db.entity.TransactionEntity
import com.econect.app.data.local.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        RecyclableMaterialEntity::class,
        RouteEntity::class,
        TransactionEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class EConectDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recyclableMaterialDao(): RecyclableMaterialDao
    abstract fun routeDao(): RouteDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        const val DATABASE_NAME = "econect_db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE users ADD COLUMN preferredRecyclingCenterId TEXT"
                )
            }
        }
    }
}
