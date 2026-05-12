package com.econect.app.domain.usecase;

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
public final class UpdateMaterialStatusUseCase_Factory implements Factory<UpdateMaterialStatusUseCase> {
  private final Provider<MaterialRepository> repositoryProvider;

  private UpdateMaterialStatusUseCase_Factory(Provider<MaterialRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpdateMaterialStatusUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpdateMaterialStatusUseCase_Factory create(
      Provider<MaterialRepository> repositoryProvider) {
    return new UpdateMaterialStatusUseCase_Factory(repositoryProvider);
  }

  public static UpdateMaterialStatusUseCase newInstance(MaterialRepository repository) {
    return new UpdateMaterialStatusUseCase(repository);
  }
}
