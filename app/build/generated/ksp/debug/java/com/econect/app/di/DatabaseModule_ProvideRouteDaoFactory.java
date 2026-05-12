package com.econect.app.di;

import com.econect.app.data.local.db.EConectDatabase;
import com.econect.app.data.local.db.dao.RouteDao;
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
public final class DatabaseModule_ProvideRouteDaoFactory implements Factory<RouteDao> {
  private final Provider<EConectDatabase> dbProvider;

  private DatabaseModule_ProvideRouteDaoFactory(Provider<EConectDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public RouteDao get() {
    return provideRouteDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideRouteDaoFactory create(Provider<EConectDatabase> dbProvider) {
    return new DatabaseModule_ProvideRouteDaoFactory(dbProvider);
  }

  public static RouteDao provideRouteDao(EConectDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRouteDao(db));
  }
}
