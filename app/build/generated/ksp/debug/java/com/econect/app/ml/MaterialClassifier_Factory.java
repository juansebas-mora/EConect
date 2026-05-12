package com.econect.app.ml;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class MaterialClassifier_Factory implements Factory<MaterialClassifier> {
  @Override
  public MaterialClassifier get() {
    return newInstance();
  }

  public static MaterialClassifier_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MaterialClassifier newInstance() {
    return new MaterialClassifier();
  }

  private static final class InstanceHolder {
    static final MaterialClassifier_Factory INSTANCE = new MaterialClassifier_Factory();
  }
}
