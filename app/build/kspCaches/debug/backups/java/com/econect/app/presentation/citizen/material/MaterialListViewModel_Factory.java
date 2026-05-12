package com.econect.app.presentation.citizen.material;

import android.content.Context;
import com.econect.app.data.local.datastore.UserDataStore;
import com.econect.app.domain.repository.MaterialRepository;
import com.econect.app.domain.usecase.GetCitizenMaterialsUseCase;
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
public final class MaterialListViewModel_Factory implements Factory<MaterialListViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<UserDataStore> userDataStoreProvider;

  private final Provider<GetCitizenMaterialsUseCase> getCitizenMaterialsUseCaseProvider;

  private final Provider<MaterialRepository> materialRepositoryProvider;

  private MaterialListViewModel_Factory(Provider<Context> contextProvider,
      Provider<UserDataStore> userDataStoreProvider,
      Provider<GetCitizenMaterialsUseCase> getCitizenMaterialsUseCaseProvider,
      Provider<MaterialRepository> materialRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.userDataStoreProvider = userDataStoreProvider;
    this.getCitizenMaterialsUseCaseProvider = getCitizenMaterialsUseCaseProvider;
    this.materialRepositoryProvider = materialRepositoryProvider;
  }

  @Override
  public MaterialListViewModel get() {
    return newInstance(contextProvider.get(), userDataStoreProvider.get(), getCitizenMaterialsUseCaseProvider.get(), materialRepositoryProvider.get());
  }

  public static MaterialListViewModel_Factory create(Provider<Context> contextProvider,
      Provider<UserDataStore> userDataStoreProvider,
      Provider<GetCitizenMaterialsUseCase> getCitizenMaterialsUseCaseProvider,
      Provider<MaterialRepository> materialRepositoryProvider) {
    return new MaterialListViewModel_Factory(contextProvider, userDataStoreProvider, getCitizenMaterialsUseCaseProvider, materialRepositoryProvider);
  }

  public static MaterialListViewModel newInstance(Context context, UserDataStore userDataStore,
      GetCitizenMaterialsUseCase getCitizenMaterialsUseCase,
      MaterialRepository materialRepository) {
    return new MaterialListViewModel(context, userDataStore, getCitizenMaterialsUseCase, materialRepository);
  }
}
