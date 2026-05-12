package com.econect.app.presentation.citizen.profile;

import com.econect.app.data.local.datastore.UserDataStore;
import com.econect.app.domain.usecase.GetUserProfileUseCase;
import com.econect.app.domain.usecase.ManageLocationsUseCase;
import com.econect.app.domain.usecase.ManageSchedulesUseCase;
import com.econect.app.domain.usecase.UpdateProfileUseCase;
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
public final class CitizenProfileViewModel_Factory implements Factory<CitizenProfileViewModel> {
  private final Provider<UserDataStore> userDataStoreProvider;

  private final Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider;

  private final Provider<UpdateProfileUseCase> updateProfileUseCaseProvider;

  private final Provider<ManageLocationsUseCase> manageLocationsUseCaseProvider;

  private final Provider<ManageSchedulesUseCase> manageSchedulesUseCaseProvider;

  private CitizenProfileViewModel_Factory(Provider<UserDataStore> userDataStoreProvider,
      Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider,
      Provider<UpdateProfileUseCase> updateProfileUseCaseProvider,
      Provider<ManageLocationsUseCase> manageLocationsUseCaseProvider,
      Provider<ManageSchedulesUseCase> manageSchedulesUseCaseProvider) {
    this.userDataStoreProvider = userDataStoreProvider;
    this.getUserProfileUseCaseProvider = getUserProfileUseCaseProvider;
    this.updateProfileUseCaseProvider = updateProfileUseCaseProvider;
    this.manageLocationsUseCaseProvider = manageLocationsUseCaseProvider;
    this.manageSchedulesUseCaseProvider = manageSchedulesUseCaseProvider;
  }

  @Override
  public CitizenProfileViewModel get() {
    return newInstance(userDataStoreProvider.get(), getUserProfileUseCaseProvider.get(), updateProfileUseCaseProvider.get(), manageLocationsUseCaseProvider.get(), manageSchedulesUseCaseProvider.get());
  }

  public static CitizenProfileViewModel_Factory create(
      Provider<UserDataStore> userDataStoreProvider,
      Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider,
      Provider<UpdateProfileUseCase> updateProfileUseCaseProvider,
      Provider<ManageLocationsUseCase> manageLocationsUseCaseProvider,
      Provider<ManageSchedulesUseCase> manageSchedulesUseCaseProvider) {
    return new CitizenProfileViewModel_Factory(userDataStoreProvider, getUserProfileUseCaseProvider, updateProfileUseCaseProvider, manageLocationsUseCaseProvider, manageSchedulesUseCaseProvider);
  }

  public static CitizenProfileViewModel newInstance(UserDataStore userDataStore,
      GetUserProfileUseCase getUserProfileUseCase, UpdateProfileUseCase updateProfileUseCase,
      ManageLocationsUseCase manageLocationsUseCase,
      ManageSchedulesUseCase manageSchedulesUseCase) {
    return new CitizenProfileViewModel(userDataStore, getUserProfileUseCase, updateProfileUseCase, manageLocationsUseCase, manageSchedulesUseCase);
  }
}
