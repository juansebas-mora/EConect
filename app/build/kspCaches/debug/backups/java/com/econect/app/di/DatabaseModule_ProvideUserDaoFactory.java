package com.econect.app.di;

import com.econect.app.data.local.db.EConectDatabase;
import com.econect.app.data.local.db.dao.UserDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideUserDaoFactory implements Factory<UserDao> {
  private final Provider<EConectDatabase> dbProvider;

  private DatabaseModule_ProvideUserDaoFactory(Provider<EConectDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public UserDao get() {
    return provideUserDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideUserDaoFactory create(Provider<EConectDatabase> dbProvider) {
    return new DatabaseModule_ProvideUserDaoFactory(dbProvider);
  }

  public static UserDao provideUserDao(EConectDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideUserDao(db));
  }
}
