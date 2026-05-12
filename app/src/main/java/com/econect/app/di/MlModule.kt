package com.econect.app.di

import android.content.Context
import com.econect.app.ml.MaterialClassifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MlModule {

    @Provides
    @Singleton
    fun provideMaterialClassifier(
        @ApplicationContext context: Context
    ): MaterialClassifier = MaterialClassifier().also { it.initialize(context) }
}
