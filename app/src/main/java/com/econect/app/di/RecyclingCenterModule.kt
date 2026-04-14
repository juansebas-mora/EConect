package com.econect.app.di

import com.econect.app.data.repository.RecyclingCenterRepositoryImpl
import com.econect.app.domain.repository.RecyclingCenterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RecyclingCenterModule {

    @Binds
    @Singleton
    abstract fun bindRecyclingCenterRepository(
        impl: RecyclingCenterRepositoryImpl
    ): RecyclingCenterRepository
}
