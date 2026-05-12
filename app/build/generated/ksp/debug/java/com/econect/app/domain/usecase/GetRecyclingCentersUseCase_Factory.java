package com.econect.app.domain.usecase;

import com.econect.app.domain.repository.RecyclingCenterRepository;
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
public final class GetRecyclingCentersUseCase_Factory implements Factory<GetRecyclingCentersUseCase> {
  private final Provider<RecyclingCenterRepository> repositoryProvider;

  private GetRecyclingCentersUseCase_Factory(
      Provider<RecyclingCenterRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetRecyclingCentersUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetRecyclingCentersUseCase_Factory create(
      Provider<RecyclingCenterRepository> repositoryProvider) {
    return new GetRecyclingCentersUseCase_Factory(repositoryProvider);
  }

  public static GetRecyclingCentersUseCase newInstance(RecyclingCenterRepository repository) {
    return new GetRecyclingCentersUseCase(repository);
  }
}
