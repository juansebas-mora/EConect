package com.econect.app.presentation.recycler.materials;

import com.econect.app.data.local.datastore.UserDataStore;
import com.econect.app.domain.repository.MaterialRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class AvailableMaterialsViewModel_Factory implements Factory<AvailableMaterialsViewModel> {
  private final Provider<MaterialRepository> materialRepositoryProvider;

  private final Provider<UserDataStore> userDataStoreProvider;

  private AvailableMaterialsViewModel_Factory(
      Provider<MaterialRepository> materialRepositoryProvider,
      Provider<UserDataStore> userDataStoreProvider) {
    this.materialRepositoryProvider = materialRepositoryProvider;
    this.userDataStoreProvider = userDataStoreProvider;
  }

  @Override
  public AvailableMaterialsViewModel get() {
    return newInstance(materialRepositoryProvider.get(), userDataStoreProvider.get());
  }

  public static AvailableMaterialsViewModel_Factory create(
      Provider<MaterialRepository> materialRepositoryProvider,
      Provider<UserDataStore> userDataStoreProvider) {
    return new AvailableMaterialsViewModel_Factory(materialRepositoryProvider, userDataStoreProvider);
  }

  public static AvailableMaterialsViewModel newInstance(MaterialRepository materialRepository,
      UserDataStore userDataStore) {
    return new AvailableMaterialsViewModel(materialRepository, userDataStore);
  }
}
