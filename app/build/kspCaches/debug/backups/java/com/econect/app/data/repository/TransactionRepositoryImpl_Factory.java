package com.econect.app.data.repository;

import com.econect.app.data.local.db.dao.TransactionDao;
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
public final class TransactionRepositoryImpl_Factory implements Factory<TransactionRepositoryImpl> {
  private final Provider<TransactionDao> daoProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private TransactionRepositoryImpl_Factory(Provider<TransactionDao> daoProvider,
      Provider<FirebaseFirestore> firestoreProvider) {
    this.daoProvider = daoProvider;
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public TransactionRepositoryImpl get() {
    return newInstance(daoProvider.get(), firestoreProvider.get());
  }

  public static TransactionRepositoryImpl_Factory create(Provider<TransactionDao> daoProvider,
      Provider<FirebaseFirestore> firestoreProvider) {
    return new TransactionRepositoryImpl_Factory(daoProvider, firestoreProvider);
  }

  public static TransactionRepositoryImpl newInstance(TransactionDao dao,
      FirebaseFirestore firestore) {
    return new TransactionRepositoryImpl(dao, firestore);
  }
}
