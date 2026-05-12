package com.econect.app.di;

import com.econect.app.data.local.db.EConectDatabase;
import com.econect.app.data.local.db.dao.RecyclableMaterialDao;
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
public final class DatabaseModule_ProvideRecyclableMaterialDaoFactory implements Factory<RecyclableMaterialDao> {
  private final Provider<EConectDatabase> dbProvider;

  private DatabaseModule_ProvideRecyclableMaterialDaoFactory(Provider<EConectDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public RecyclableMaterialDao get() {
    return provideRecyclableMaterialDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideRecyclableMaterialDaoFactory create(
      Provider<EConectDatabase> dbProvider) {
    return new DatabaseModule_ProvideRecyclableMaterialDaoFactory(dbProvider);
  }

  public static RecyclableMaterialDao provideRecyclableMaterialDao(EConectDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRecyclableMaterialDao(db));
  }
}
