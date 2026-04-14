package com.econect.app.di

import android.content.Context
import androidx.room.Room
import com.econect.app.data.local.db.EConectDatabase
import com.econect.app.data.local.db.EConectDatabase.Companion.MIGRATION_1_2
import com.econect.app.data.local.db.dao.RecyclableMaterialDao
import com.econect.app.data.local.db.dao.RouteDao
import com.econect.app.data.local.db.dao.TransactionDao
import com.econect.app.data.local.db.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EConectDatabase =
        Room.databaseBuilder(context, EConectDatabase::class.java, EConectDatabase.DATABASE_NAME)
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideUserDao(db: EConectDatabase): UserDao = db.userDao()

    @Provides
    fun provideRecyclableMaterialDao(db: EConectDatabase): RecyclableMaterialDao =
        db.recyclableMaterialDao()

    @Provides
    fun provideRouteDao(db: EConectDatabase): RouteDao = db.routeDao()

    @Provides
    fun provideTransactionDao(db: EConectDatabase): TransactionDao = db.transactionDao()
}
