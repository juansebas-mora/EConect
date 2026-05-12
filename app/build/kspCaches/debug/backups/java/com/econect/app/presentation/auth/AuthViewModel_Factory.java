package com.econect.app.presentation.auth;

import com.econect.app.data.local.datastore.UserDataStore;
import com.econect.app.domain.usecase.GetCurrentUserUseCase;
import com.econect.app.domain.usecase.LoginUseCase;
import com.econect.app.domain.usecase.LogoutUseCase;
import com.econect.app.domain.usecase.RegisterUseCase;
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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<LoginUseCase> loginUseCaseProvider;

  private final Provider<RegisterUseCase> registerUseCaseProvider;

  private final Provider<LogoutUseCase> logoutUseCaseProvider;

  private final Provider<GetCurrentUserUseCase> getCurrentUserUseCaseProvider;

  private final Provider<UserDataStore> userDataStoreProvider;

  private AuthViewModel_Factory(Provider<LoginUseCase> loginUseCaseProvider,
      Provider<RegisterUseCase> registerUseCaseProvider,
      Provider<LogoutUseCase> logoutUseCaseProvider,
      Provider<GetCurrentUserUseCase> getCurrentUserUseCaseProvider,
      Provider<UserDataStore> userDataStoreProvider) {
    this.loginUseCaseProvider = loginUseCaseProvider;
    this.registerUseCaseProvider = registerUseCaseProvider;
    this.logoutUseCaseProvider = logoutUseCaseProvider;
    this.getCurrentUserUseCaseProvider = getCurrentUserUseCaseProvider;
    this.userDataStoreProvider = userDataStoreProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(loginUseCaseProvider.get(), registerUseCaseProvider.get(), logoutUseCaseProvider.get(), getCurrentUserUseCaseProvider.get(), userDataStoreProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<LoginUseCase> loginUseCaseProvider,
      Provider<RegisterUseCase> registerUseCaseProvider,
      Provider<LogoutUseCase> logoutUseCaseProvider,
      Provider<GetCurrentUserUseCase> getCurrentUserUseCaseProvider,
      Provider<UserDataStore> userDataStoreProvider) {
    return new AuthViewModel_Factory(loginUseCaseProvider, registerUseCaseProvider, logoutUseCaseProvider, getCurrentUserUseCaseProvider, userDataStoreProvider);
  }

  public static AuthViewModel newInstance(LoginUseCase loginUseCase,
      RegisterUseCase registerUseCase, LogoutUseCase logoutUseCase,
      GetCurrentUserUseCase getCurrentUserUseCase, UserDataStore userDataStore) {
    return new AuthViewModel(loginUseCase, registerUseCase, logoutUseCase, getCurrentUserUseCase, userDataStore);
  }
}
