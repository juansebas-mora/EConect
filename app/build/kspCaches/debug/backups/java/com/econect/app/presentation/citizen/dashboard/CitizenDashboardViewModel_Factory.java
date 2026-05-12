package com.econect.app.presentation.citizen.dashboard;

import com.econect.app.data.local.datastore.UserDataStore;
import com.econect.app.domain.repository.MaterialRepository;
import com.econect.app.domain.usecase.GetCitizenMaterialsUseCase;
import com.econect.app.domain.usecase.GetUserProfileUseCase;
import com.econect.app.domain.usecase.LogoutUseCase;
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
public final class CitizenDashboardViewModel_Factory implements Factory<CitizenDashboardViewModel> {
  private final Provider<UserDataStore> userDataStoreProvider;

  private final Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider;

  private final Provider<GetCitizenMaterialsUseCase> getCitizenMaterialsUseCaseProvider;

  private final Provider<MaterialRepository> materialRepositoryProvider;

  private final Provider<LogoutUseCase> logoutUseCaseProvider;

  private CitizenDashboardViewModel_Factory(Provider<UserDataStore> userDataStoreProvider,
      Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider,
      Provider<GetCitizenMaterialsUseCase> getCitizenMaterialsUseCaseProvider,
      Provider<MaterialRepository> materialRepositoryProvider,
      Provider<LogoutUseCase> logoutUseCaseProvider) {
    this.userDataStoreProvider = userDataStoreProvider;
    this.getUserProfileUseCaseProvider = getUserProfileUseCaseProvider;
    this.getCitizenMaterialsUseCaseProvider = getCitizenMaterialsUseCaseProvider;
    this.materialRepositoryProvider = materialRepositoryProvider;
    this.logoutUseCaseProvider = logoutUseCaseProvider;
  }

  @Override
  public CitizenDashboardViewModel get() {
    return newInstance(userDataStoreProvider.get(), getUserProfileUseCaseProvider.get(), getCitizenMaterialsUseCaseProvider.get(), materialRepositoryProvider.get(), logoutUseCaseProvider.get());
  }

  public static CitizenDashboardViewModel_Factory create(
      Provider<UserDataStore> userDataStoreProvider,
      Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider,
      Provider<GetCitizenMaterialsUseCase> getCitizenMaterialsUseCaseProvider,
      Provider<MaterialRepository> materialRepositoryProvider,
      Provider<LogoutUseCase> logoutUseCaseProvider) {
    return new CitizenDashboardViewModel_Factory(userDataStoreProvider, getUserProfileUseCaseProvider, getCitizenMaterialsUseCaseProvider, materialRepositoryProvider, logoutUseCaseProvider);
  }

  public static CitizenDashboardViewModel newInstance(UserDataStore userDataStore,
      GetUserProfileUseCase getUserProfileUseCase,
      GetCitizenMaterialsUseCase getCitizenMaterialsUseCase, MaterialRepository materialRepository,
      LogoutUseCase logoutUseCase) {
    return new CitizenDashboardViewModel(userDataStore, getUserProfileUseCase, getCitizenMaterialsUseCase, materialRepository, logoutUseCase);
  }
}
