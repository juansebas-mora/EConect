package com.econect.app.data.repository;

import com.econect.app.data.local.db.dao.UserDao;
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
public final class UserRepositoryImpl_Factory implements Factory<UserRepositoryImpl> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<UserDao> userDaoProvider;

  private UserRepositoryImpl_Factory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<UserDao> userDaoProvider) {
    this.firestoreProvider = firestoreProvider;
    this.userDaoProvider = userDaoProvider;
  }

  @Override
  public UserRepositoryImpl get() {
    return newInstance(firestoreProvider.get(), userDaoProvider.get());
  }

  public static UserRepositoryImpl_Factory create(Provider<FirebaseFirestore> firestoreProvider,
      Provider<UserDao> userDaoProvider) {
    return new UserRepositoryImpl_Factory(firestoreProvider, userDaoProvider);
  }

  public static UserRepositoryImpl newInstance(FirebaseFirestore firestore, UserDao userDao) {
    return new UserRepositoryImpl(firestore, userDao);
  }
}
