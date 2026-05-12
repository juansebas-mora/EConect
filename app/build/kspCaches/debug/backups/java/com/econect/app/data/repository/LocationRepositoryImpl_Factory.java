package com.econect.app.data.repository;

import android.content.Context;
import com.econect.app.domain.repository.RecyclingCenterRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class LocationRepositoryImpl_Factory implements Factory<LocationRepositoryImpl> {
  private final Provider<FusedLocationProviderClient> fusedLocationClientProvider;

  private final Provider<RecyclingCenterRepository> recyclingCenterRepositoryProvider;

  private final Provider<Context> contextProvider;

  private LocationRepositoryImpl_Factory(
      Provider<FusedLocationProviderClient> fusedLocationClientProvider,
      Provider<RecyclingCenterRepository> recyclingCenterRepositoryProvider,
      Provider<Context> contextProvider) {
    this.fusedLocationClientProvider = fusedLocationClientProvider;
    this.recyclingCenterRepositoryProvider = recyclingCenterRepositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public LocationRepositoryImpl get() {
    return newInstance(fusedLocationClientProvider.get(), recyclingCenterRepositoryProvider.get(), contextProvider.get());
  }

  public static LocationRepositoryImpl_Factory create(
      Provider<FusedLocationProviderClient> fusedLocationClientProvider,
      Provider<RecyclingCenterRepository> recyclingCenterRepositoryProvider,
      Provider<Context> contextProvider) {
    return new LocationRepositoryImpl_Factory(fusedLocationClientProvider, recyclingCenterRepositoryProvider, contextProvider);
  }

  public static LocationRepositoryImpl newInstance(FusedLocationProviderClient fusedLocationClient,
      RecyclingCenterRepository recyclingCenterRepository, Context context) {
    return new LocationRepositoryImpl(fusedLocationClient, recyclingCenterRepository, context);
  }
}
