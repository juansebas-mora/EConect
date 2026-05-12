package com.econect.app.data.repository;

import com.econect.app.data.local.db.dao.RecyclableMaterialDao;
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
public final class MaterialRepositoryImpl_Factory implements Factory<MaterialRepositoryImpl> {
  private final Provider<RecyclableMaterialDao> daoProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private MaterialRepositoryImpl_Factory(Provider<RecyclableMaterialDao> daoProvider,
      Provider<FirebaseFirestore> firestoreProvider) {
    this.daoProvider = daoProvider;
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public MaterialRepositoryImpl get() {
    return newInstance(daoProvider.get(), firestoreProvider.get());
  }

  public static MaterialRepositoryImpl_Factory create(Provider<RecyclableMaterialDao> daoProvider,
      Provider<FirebaseFirestore> firestoreProvider) {
    return new MaterialRepositoryImpl_Factory(daoProvider, firestoreProvider);
  }

  public static MaterialRepositoryImpl newInstance(RecyclableMaterialDao dao,
      FirebaseFirestore firestore) {
    return new MaterialRepositoryImpl(dao, firestore);
  }
}
