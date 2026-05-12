package com.econect.app.presentation.recycler.profile;

import com.econect.app.data.local.datastore.UserDataStore;
import com.econect.app.domain.usecase.GetRecyclingCentersUseCase;
import com.econect.app.domain.usecase.GetUserProfileUseCase;
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
public final class RecyclerProfileViewModel_Factory implements Factory<RecyclerProfileViewModel> {
  private final Provider<UserDataStore> userDataStoreProvider;

  private final Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider;

  private final Provider<UpdateProfileUseCase> updateProfileUseCaseProvider;

  private final Provider<ManageSchedulesUseCase> manageSchedulesUseCaseProvider;

  private final Provider<GetRecyclingCentersUseCase> getRecyclingCentersUseCaseProvider;

  private RecyclerProfileViewModel_Factory(Provider<UserDataStore> userDataStoreProvider,
      Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider,
      Provider<UpdateProfileUseCase> updateProfileUseCaseProvider,
      Provider<ManageSchedulesUseCase> manageSchedulesUseCaseProvider,
      Provider<GetRecyclingCentersUseCase> getRecyclingCentersUseCaseProvider) {
    this.userDataStoreProvider = userDataStoreProvider;
    this.getUserProfileUseCaseProvider = getUserProfileUseCaseProvider;
    this.updateProfileUseCaseProvider = updateProfileUseCaseProvider;
    this.manageSchedulesUseCaseProvider = manageSchedulesUseCaseProvider;
    this.getRecyclingCentersUseCaseProvider = getRecyclingCentersUseCaseProvider;
  }

  @Override
  public RecyclerProfileViewModel get() {
    return newInstance(userDataStoreProvider.get(), getUserProfileUseCaseProvider.get(), updateProfileUseCaseProvider.get(), manageSchedulesUseCaseProvider.get(), getRecyclingCentersUseCaseProvider.get());
  }

  public static RecyclerProfileViewModel_Factory create(
      Provider<UserDataStore> userDataStoreProvider,
      Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider,
      Provider<UpdateProfileUseCase> updateProfileUseCaseProvider,
      Provider<ManageSchedulesUseCase> manageSchedulesUseCaseProvider,
      Provider<GetRecyclingCentersUseCase> getRecyclingCentersUseCaseProvider) {
    return new RecyclerProfileViewModel_Factory(userDataStoreProvider, getUserProfileUseCaseProvider, updateProfileUseCaseProvider, manageSchedulesUseCaseProvider, getRecyclingCentersUseCaseProvider);
  }

  public static RecyclerProfileViewModel newInstance(UserDataStore userDataStore,
      GetUserProfileUseCase getUserProfileUseCase, UpdateProfileUseCase updateProfileUseCase,
      ManageSchedulesUseCase manageSchedulesUseCase,
      GetRecyclingCentersUseCase getRecyclingCentersUseCase) {
    return new RecyclerProfileViewModel(userDataStore, getUserProfileUseCase, updateProfileUseCase, manageSchedulesUseCase, getRecyclingCentersUseCase);
  }
}
