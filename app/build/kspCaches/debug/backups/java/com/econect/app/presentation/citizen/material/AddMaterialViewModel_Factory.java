package com.econect.app.presentation.citizen.material;

import com.econect.app.data.local.datastore.UserDataStore;
import com.econect.app.domain.usecase.AddMaterialUseCase;
import com.econect.app.domain.usecase.GetUserProfileUseCase;
import com.econect.app.ml.MaterialClassifier;
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
public final class AddMaterialViewModel_Factory implements Factory<AddMaterialViewModel> {
  private final Provider<UserDataStore> userDataStoreProvider;

  private final Provider<AddMaterialUseCase> addMaterialUseCaseProvider;

  private final Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider;

  private final Provider<MaterialClassifier> materialClassifierProvider;

  private AddMaterialViewModel_Factory(Provider<UserDataStore> userDataStoreProvider,
      Provider<AddMaterialUseCase> addMaterialUseCaseProvider,
      Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider,
      Provider<MaterialClassifier> materialClassifierProvider) {
    this.userDataStoreProvider = userDataStoreProvider;
    this.addMaterialUseCaseProvider = addMaterialUseCaseProvider;
    this.getUserProfileUseCaseProvider = getUserProfileUseCaseProvider;
    this.materialClassifierProvider = materialClassifierProvider;
  }

  @Override
  public AddMaterialViewModel get() {
    return newInstance(userDataStoreProvider.get(), addMaterialUseCaseProvider.get(), getUserProfileUseCaseProvider.get(), materialClassifierProvider.get());
  }

  public static AddMaterialViewModel_Factory create(Provider<UserDataStore> userDataStoreProvider,
      Provider<AddMaterialUseCase> addMaterialUseCaseProvider,
      Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider,
      Provider<MaterialClassifier> materialClassifierProvider) {
    return new AddMaterialViewModel_Factory(userDataStoreProvider, addMaterialUseCaseProvider, getUserProfileUseCaseProvider, materialClassifierProvider);
  }

  public static AddMaterialViewModel newInstance(UserDataStore userDataStore,
      AddMaterialUseCase addMaterialUseCase, GetUserProfileUseCase getUserProfileUseCase,
      MaterialClassifier materialClassifier) {
    return new AddMaterialViewModel(userDataStore, addMaterialUseCase, getUserProfileUseCase, materialClassifier);
  }
}
