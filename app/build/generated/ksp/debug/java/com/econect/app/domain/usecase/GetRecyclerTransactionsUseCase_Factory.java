package com.econect.app.domain.usecase;

import com.econect.app.domain.repository.TransactionRepository;
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
public final class GetRecyclerTransactionsUseCase_Factory implements Factory<GetRecyclerTransactionsUseCase> {
  private final Provider<TransactionRepository> repositoryProvider;

  private GetRecyclerTransactionsUseCase_Factory(
      Provider<TransactionRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetRecyclerTransactionsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetRecyclerTransactionsUseCase_Factory create(
      Provider<TransactionRepository> repositoryProvider) {
    return new GetRecyclerTransactionsUseCase_Factory(repositoryProvider);
  }

  public static GetRecyclerTransactionsUseCase newInstance(TransactionRepository repository) {
    return new GetRecyclerTransactionsUseCase(repository);
  }
}
