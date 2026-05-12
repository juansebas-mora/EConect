package com.econect.app.domain.usecase;

import com.econect.app.domain.repository.UserRepository;
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
public final class ManageLocationsUseCase_Factory implements Factory<ManageLocationsUseCase> {
  private final Provider<UserRepository> userRepositoryProvider;

  private ManageLocationsUseCase_Factory(Provider<UserRepository> userRepositoryProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public ManageLocationsUseCase get() {
    return newInstance(userRepositoryProvider.get());
  }

  public static ManageLocationsUseCase_Factory create(
      Provider<UserRepository> userRepositoryProvider) {
    return new ManageLocationsUseCase_Factory(userRepositoryProvider);
  }

  public static ManageLocationsUseCase newInstance(UserRepository userRepository) {
    return new ManageLocationsUseCase(userRepository);
  }
}
