package com.econect.app.domain.usecase;

import com.econect.app.domain.repository.RouteRepository;
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
public final class GetRecyclerRoutesUseCase_Factory implements Factory<GetRecyclerRoutesUseCase> {
  private final Provider<RouteRepository> repositoryProvider;

  private GetRecyclerRoutesUseCase_Factory(Provider<RouteRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetRecyclerRoutesUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetRecyclerRoutesUseCase_Factory create(
      Provider<RouteRepository> repositoryProvider) {
    return new GetRecyclerRoutesUseCase_Factory(repositoryProvider);
  }

  public static GetRecyclerRoutesUseCase newInstance(RouteRepository repository) {
    return new GetRecyclerRoutesUseCase(repository);
  }
}
