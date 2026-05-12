package com.econect.app.data.repository;

import com.econect.app.data.local.db.dao.RouteDao;
import com.google.firebase.firestore.FirebaseFirestore;
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
public final class RouteRepositoryImpl_Factory implements Factory<RouteRepositoryImpl> {
  private final Provider<RouteDao> daoProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private RouteRepositoryImpl_Factory(Provider<RouteDao> daoProvider,
      Provider<FirebaseFirestore> firestoreProvider) {
    this.daoProvider = daoProvider;
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public RouteRepositoryImpl get() {
    return newInstance(daoProvider.get(), firestoreProvider.get());
  }

  public static RouteRepositoryImpl_Factory create(Provider<RouteDao> daoProvider,
      Provider<FirebaseFirestore> firestoreProvider) {
    return new RouteRepositoryImpl_Factory(daoProvider, firestoreProvider);
  }

  public static RouteRepositoryImpl newInstance(RouteDao dao, FirebaseFirestore firestore) {
    return new RouteRepositoryImpl(dao, firestore);
  }
}
