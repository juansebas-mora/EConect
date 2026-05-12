package com.econect.app.presentation.recycler.dashboard;

import com.econect.app.data.local.datastore.UserDataStore;
import com.econect.app.domain.repository.LocationRepository;
import com.econect.app.domain.repository.MaterialRepository;
import com.econect.app.domain.repository.RouteRepository;
import com.econect.app.domain.repository.TransactionRepository;
import com.econect.app.domain.usecase.GetRecyclerRoutesUseCase;
import com.econect.app.domain.usecase.GetRecyclerTransactionsUseCase;
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
public final class RecyclerDashboardViewModel_Factory implements Factory<RecyclerDashboardViewModel> {
  private final Provider<UserDataStore> userDataStoreProvider;

  private final Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider;

  private final Provider<GetRecyclerRoutesUseCase> getRecyclerRoutesUseCaseProvider;

  private final Provider<GetRecyclerTransactionsUseCase> getRecyclerTransactionsUseCaseProvider;

  private final Provider<RouteRepository> routeRepositoryProvider;

  private final Provider<TransactionRepository> transactionRepositoryProvider;

  private final Provider<MaterialRepository> materialRepositoryProvider;

  private final Provider<LogoutUseCase> logoutUseCaseProvider;

  private final Provider<LocationRepository> locationRepositoryProvider;

  private RecyclerDashboardViewModel_Factory(Provider<UserDataStore> userDataStoreProvider,
      Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider,
      Provider<GetRecyclerRoutesUseCase> getRecyclerRoutesUseCaseProvider,
      Provider<GetRecyclerTransactionsUseCase> getRecyclerTransactionsUseCaseProvider,
      Provider<RouteRepository> routeRepositoryProvider,
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<MaterialRepository> materialRepositoryProvider,
      Provider<LogoutUseCase> logoutUseCaseProvider,
      Provider<LocationRepository> locationRepositoryProvider) {
    this.userDataStoreProvider = userDataStoreProvider;
    this.getUserProfileUseCaseProvider = getUserProfileUseCaseProvider;
    this.getRecyclerRoutesUseCaseProvider = getRecyclerRoutesUseCaseProvider;
    this.getRecyclerTransactionsUseCaseProvider = getRecyclerTransactionsUseCaseProvider;
    this.routeRepositoryProvider = routeRepositoryProvider;
    this.transactionRepositoryProvider = transactionRepositoryProvider;
    this.materialRepositoryProvider = materialRepositoryProvider;
    this.logoutUseCaseProvider = logoutUseCaseProvider;
    this.locationRepositoryProvider = locationRepositoryProvider;
  }

  @Override
  public RecyclerDashboardViewModel get() {
    return newInstance(userDataStoreProvider.get(), getUserProfileUseCaseProvider.get(), getRecyclerRoutesUseCaseProvider.get(), getRecyclerTransactionsUseCaseProvider.get(), routeRepositoryProvider.get(), transactionRepositoryProvider.get(), materialRepositoryProvider.get(), logoutUseCaseProvider.get(), locationRepositoryProvider.get());
  }

  public static RecyclerDashboardViewModel_Factory create(
      Provider<UserDataStore> userDataStoreProvider,
      Provider<GetUserProfileUseCase> getUserProfileUseCaseProvider,
      Provider<GetRecyclerRoutesUseCase> getRecyclerRoutesUseCaseProvider,
      Provider<GetRecyclerTransactionsUseCase> getRecyclerTransactionsUseCaseProvider,
      Provider<RouteRepository> routeRepositoryProvider,
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<MaterialRepository> materialRepositoryProvider,
      Provider<LogoutUseCase> logoutUseCaseProvider,
      Provider<LocationRepository> locationRepositoryProvider) {
    return new RecyclerDashboardViewModel_Factory(userDataStoreProvider, getUserProfileUseCaseProvider, getRecyclerRoutesUseCaseProvider, getRecyclerTransactionsUseCaseProvider, routeRepositoryProvider, transactionRepositoryProvider, materialRepositoryProvider, logoutUseCaseProvider, locationRepositoryProvider);
  }

  public static RecyclerDashboardViewModel newInstance(UserDataStore userDataStore,
      GetUserProfileUseCase getUserProfileUseCase,
      GetRecyclerRoutesUseCase getRecyclerRoutesUseCase,
      GetRecyclerTransactionsUseCase getRecyclerTransactionsUseCase,
      RouteRepository routeRepository, TransactionRepository transactionRepository,
      MaterialRepository materialRepository, LogoutUseCase logoutUseCase,
      LocationRepository locationRepository) {
    return new RecyclerDashboardViewModel(userDataStore, getUserProfileUseCase, getRecyclerRoutesUseCase, getRecyclerTransactionsUseCase, routeRepository, transactionRepository, materialRepository, logoutUseCase, locationRepository);
  }
}
