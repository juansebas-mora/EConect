package com.econect.app.di

import com.econect.app.data.repository.MaterialRepositoryImpl
import com.econect.app.domain.repository.MaterialRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MaterialModule {

    @Binds
    @Singleton
    abstract fun bindMaterialRepository(impl: MaterialRepositoryImpl): MaterialRepository
}
