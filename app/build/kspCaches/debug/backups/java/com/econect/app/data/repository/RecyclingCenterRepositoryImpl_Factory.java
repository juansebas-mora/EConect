package com.econect.app.data.repository;

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
public final class RecyclingCenterRepositoryImpl_Factory implements Factory<RecyclingCenterRepositoryImpl> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private RecyclingCenterRepositoryImpl_Factory(Provider<FirebaseFirestore> firestoreProvider) {
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public RecyclingCenterRepositoryImpl get() {
    return newInstance(firestoreProvider.get());
  }

  public static RecyclingCenterRepositoryImpl_Factory create(
      Provider<FirebaseFirestore> firestoreProvider) {
    return new RecyclingCenterRepositoryImpl_Factory(firestoreProvider);
  }

  public static RecyclingCenterRepositoryImpl newInstance(FirebaseFirestore firestore) {
    return new RecyclingCenterRepositoryImpl(firestore);
  }
}
