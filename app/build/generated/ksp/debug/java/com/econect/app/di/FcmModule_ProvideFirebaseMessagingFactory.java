package com.econect.app.di;

import com.google.firebase.messaging.FirebaseMessaging;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class FcmModule_ProvideFirebaseMessagingFactory implements Factory<FirebaseMessaging> {
  @Override
  public FirebaseMessaging get() {
    return provideFirebaseMessaging();
  }

  public static FcmModule_ProvideFirebaseMessagingFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseMessaging provideFirebaseMessaging() {
    return Preconditions.checkNotNullFromProvides(FcmModule.INSTANCE.provideFirebaseMessaging());
  }

  private static final class InstanceHolder {
    static final FcmModule_ProvideFirebaseMessagingFactory INSTANCE = new FcmModule_ProvideFirebaseMessagingFactory();
  }
}
