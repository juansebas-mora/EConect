package com.econect.app.di;

import android.content.Context;
import com.econect.app.ml.MaterialClassifier;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class MlModule_ProvideMaterialClassifierFactory implements Factory<MaterialClassifier> {
  private final Provider<Context> contextProvider;

  private MlModule_ProvideMaterialClassifierFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MaterialClassifier get() {
    return provideMaterialClassifier(contextProvider.get());
  }

  public static MlModule_ProvideMaterialClassifierFactory create(
      Provider<Context> contextProvider) {
    return new MlModule_ProvideMaterialClassifierFactory(contextProvider);
  }

  public static MaterialClassifier provideMaterialClassifier(Context context) {
    return Preconditions.checkNotNullFromProvides(MlModule.INSTANCE.provideMaterialClassifier(context));
  }
}
