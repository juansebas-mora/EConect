package com.econect.app.data.remote.fcm;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class EConectFcmService_MembersInjector implements MembersInjector<EConectFcmService> {
  private final Provider<FirebaseAuth> authProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private EConectFcmService_MembersInjector(Provider<FirebaseAuth> authProvider,
      Provider<FirebaseFirestore> firestoreProvider) {
    this.authProvider = authProvider;
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public void injectMembers(EConectFcmService instance) {
    injectAuth(instance, authProvider.get());
    injectFirestore(instance, firestoreProvider.get());
  }

  public static MembersInjector<EConectFcmService> create(Provider<FirebaseAuth> authProvider,
      Provider<FirebaseFirestore> firestoreProvider) {
    return new EConectFcmService_MembersInjector(authProvider, firestoreProvider);
  }

  @InjectedFieldSignature("com.econect.app.data.remote.fcm.EConectFcmService.auth")
  public static void injectAuth(EConectFcmService instance, FirebaseAuth auth) {
    instance.auth = auth;
  }

  @InjectedFieldSignature("com.econect.app.data.remote.fcm.EConectFcmService.firestore")
  public static void injectFirestore(EConectFcmService instance, FirebaseFirestore firestore) {
    instance.firestore = firestore;
  }
}
