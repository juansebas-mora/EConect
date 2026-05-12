package com.econect.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.econect.app.data.local.datastore.UserDataStore;
import com.econect.app.data.local.db.EConectDatabase;
import com.econect.app.data.local.db.dao.RecyclableMaterialDao;
import com.econect.app.data.local.db.dao.RouteDao;
import com.econect.app.data.local.db.dao.TransactionDao;
import com.econect.app.data.local.db.dao.UserDao;
import com.econect.app.data.remote.fcm.EConectFcmService;
import com.econect.app.data.remote.fcm.EConectFcmService_MembersInjector;
import com.econect.app.data.repository.AuthRepositoryImpl;
import com.econect.app.data.repository.LocationRepositoryImpl;
import com.econect.app.data.repository.MaterialRepositoryImpl;
import com.econect.app.data.repository.RecyclingCenterRepositoryImpl;
import com.econect.app.data.repository.RouteRepositoryImpl;
import com.econect.app.data.repository.TransactionRepositoryImpl;
import com.econect.app.data.repository.UserRepositoryImpl;
import com.econect.app.di.DatabaseModule_ProvideDatabaseFactory;
import com.econect.app.di.DatabaseModule_ProvideRecyclableMaterialDaoFactory;
import com.econect.app.di.DatabaseModule_ProvideRouteDaoFactory;
import com.econect.app.di.DatabaseModule_ProvideTransactionDaoFactory;
import com.econect.app.di.DatabaseModule_ProvideUserDaoFactory;
import com.econect.app.di.FirebaseModule_ProvideFirebaseAuthFactory;
import com.econect.app.di.FirebaseModule_ProvideFirebaseFirestoreFactory;
import com.econect.app.di.LocationModule_Companion_ProvideFusedLocationProviderClientFactory;
import com.econect.app.di.MlModule_ProvideMaterialClassifierFactory;
import com.econect.app.domain.repository.AuthRepository;
import com.econect.app.domain.repository.LocationRepository;
import com.econect.app.domain.repository.MaterialRepository;
import com.econect.app.domain.repository.RecyclingCenterRepository;
import com.econect.app.domain.repository.RouteRepository;
import com.econect.app.domain.repository.TransactionRepository;
import com.econect.app.domain.repository.UserRepository;
import com.econect.app.domain.usecase.AddMaterialUseCase;
import com.econect.app.domain.usecase.GetCitizenMaterialsUseCase;
import com.econect.app.domain.usecase.GetCurrentUserUseCase;
import com.econect.app.domain.usecase.GetRecyclerRoutesUseCase;
import com.econect.app.domain.usecase.GetRecyclerTransactionsUseCase;
import com.econect.app.domain.usecase.GetRecyclingCentersUseCase;
import com.econect.app.domain.usecase.GetUserProfileUseCase;
import com.econect.app.domain.usecase.LoginUseCase;
import com.econect.app.domain.usecase.LogoutUseCase;
import com.econect.app.domain.usecase.ManageLocationsUseCase;
import com.econect.app.domain.usecase.ManageSchedulesUseCase;
import com.econect.app.domain.usecase.RegisterUseCase;
import com.econect.app.domain.usecase.UpdateProfileUseCase;
import com.econect.app.ml.MaterialClassifier;
import com.econect.app.presentation.auth.AuthViewModel;
import com.econect.app.presentation.auth.AuthViewModel_HiltModules;
import com.econect.app.presentation.auth.AuthViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.econect.app.presentation.auth.AuthViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.econect.app.presentation.citizen.dashboard.CitizenDashboardViewModel;
import com.econect.app.presentation.citizen.dashboard.CitizenDashboardViewModel_HiltModules;
import com.econect.app.presentation.citizen.dashboard.CitizenDashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.econect.app.presentation.citizen.dashboard.CitizenDashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.econect.app.presentation.citizen.material.AddMaterialViewModel;
import com.econect.app.presentation.citizen.material.AddMaterialViewModel_HiltModules;
import com.econect.app.presentation.citizen.material.AddMaterialViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.econect.app.presentation.citizen.material.AddMaterialViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.econect.app.presentation.citizen.material.MaterialListViewModel;
import com.econect.app.presentation.citizen.material.MaterialListViewModel_HiltModules;
import com.econect.app.presentation.citizen.material.MaterialListViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.econect.app.presentation.citizen.material.MaterialListViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.econect.app.presentation.citizen.profile.CitizenProfileViewModel;
import com.econect.app.presentation.citizen.profile.CitizenProfileViewModel_HiltModules;
import com.econect.app.presentation.citizen.profile.CitizenProfileViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.econect.app.presentation.citizen.profile.CitizenProfileViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.econect.app.presentation.recycler.dashboard.RecyclerDashboardViewModel;
import com.econect.app.presentation.recycler.dashboard.RecyclerDashboardViewModel_HiltModules;
import com.econect.app.presentation.recycler.dashboard.RecyclerDashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.econect.app.presentation.recycler.dashboard.RecyclerDashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.econect.app.presentation.recycler.materials.AvailableMaterialsViewModel;
import com.econect.app.presentation.recycler.materials.AvailableMaterialsViewModel_HiltModules;
import com.econect.app.presentation.recycler.materials.AvailableMaterialsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.econect.app.presentation.recycler.materials.AvailableMaterialsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.econect.app.presentation.recycler.profile.RecyclerProfileViewModel;
import com.econect.app.presentation.recycler.profile.RecyclerProfileViewModel_HiltModules;
import com.econect.app.presentation.recycler.profile.RecyclerProfileViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.econect.app.presentation.recycler.profile.RecyclerProfileViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerEConectApp_HiltComponents_SingletonC {
  private DaggerEConectApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public EConectApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements EConectApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public EConectApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements EConectApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public EConectApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements EConectApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public EConectApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements EConectApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public EConectApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements EConectApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public EConectApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements EConectApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public EConectApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements EConectApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public EConectApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends EConectApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends EConectApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    FragmentCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends EConectApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends EConectApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    ActivityCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    ImmutableMap keySetMapOfClassOfAndBooleanBuilder() {
      ImmutableMap.Builder mapBuilder = ImmutableMap.<String, Boolean>builderWithExpectedSize(8);
      mapBuilder.put(AddMaterialViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AddMaterialViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(AuthViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AuthViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(AvailableMaterialsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AvailableMaterialsViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(CitizenDashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, CitizenDashboardViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(CitizenProfileViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, CitizenProfileViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(MaterialListViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, MaterialListViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(RecyclerDashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, RecyclerDashboardViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(RecyclerProfileViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, RecyclerProfileViewModel_HiltModules.KeyModule.provide());
      return mapBuilder.build();
    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(keySetMapOfClassOfAndBooleanBuilder());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends EConectApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    Provider<AddMaterialViewModel> addMaterialViewModelProvider;

    Provider<AuthViewModel> authViewModelProvider;

    Provider<AvailableMaterialsViewModel> availableMaterialsViewModelProvider;

    Provider<CitizenDashboardViewModel> citizenDashboardViewModelProvider;

    Provider<CitizenProfileViewModel> citizenProfileViewModelProvider;

    Provider<MaterialListViewModel> materialListViewModelProvider;

    Provider<RecyclerDashboardViewModel> recyclerDashboardViewModelProvider;

    Provider<RecyclerProfileViewModel> recyclerProfileViewModelProvider;

    ViewModelCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        SavedStateHandle savedStateHandleParam, ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    AddMaterialUseCase addMaterialUseCase() {
      return new AddMaterialUseCase(singletonCImpl.bindMaterialRepositoryProvider.get());
    }

    GetUserProfileUseCase getUserProfileUseCase() {
      return new GetUserProfileUseCase(singletonCImpl.bindUserRepositoryProvider.get());
    }

    LoginUseCase loginUseCase() {
      return new LoginUseCase(singletonCImpl.bindAuthRepositoryProvider.get());
    }

    RegisterUseCase registerUseCase() {
      return new RegisterUseCase(singletonCImpl.bindAuthRepositoryProvider.get());
    }

    LogoutUseCase logoutUseCase() {
      return new LogoutUseCase(singletonCImpl.bindAuthRepositoryProvider.get());
    }

    GetCurrentUserUseCase getCurrentUserUseCase() {
      return new GetCurrentUserUseCase(singletonCImpl.bindAuthRepositoryProvider.get());
    }

    GetCitizenMaterialsUseCase getCitizenMaterialsUseCase() {
      return new GetCitizenMaterialsUseCase(singletonCImpl.bindMaterialRepositoryProvider.get());
    }

    UpdateProfileUseCase updateProfileUseCase() {
      return new UpdateProfileUseCase(singletonCImpl.bindUserRepositoryProvider.get());
    }

    ManageLocationsUseCase manageLocationsUseCase() {
      return new ManageLocationsUseCase(singletonCImpl.bindUserRepositoryProvider.get());
    }

    ManageSchedulesUseCase manageSchedulesUseCase() {
      return new ManageSchedulesUseCase(singletonCImpl.bindUserRepositoryProvider.get());
    }

    GetRecyclerRoutesUseCase getRecyclerRoutesUseCase() {
      return new GetRecyclerRoutesUseCase(singletonCImpl.bindRouteRepositoryProvider.get());
    }

    GetRecyclerTransactionsUseCase getRecyclerTransactionsUseCase() {
      return new GetRecyclerTransactionsUseCase(singletonCImpl.bindTransactionRepositoryProvider.get());
    }

    GetRecyclingCentersUseCase getRecyclingCentersUseCase() {
      return new GetRecyclingCentersUseCase(singletonCImpl.bindRecyclingCenterRepositoryProvider.get());
    }

    ImmutableMap hiltViewModelMapMapOfClassOfAndProviderOfViewModelBuilder() {
      ImmutableMap.Builder mapBuilder = ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(8);
      mapBuilder.put(AddMaterialViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (addMaterialViewModelProvider)));
      mapBuilder.put(AuthViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (authViewModelProvider)));
      mapBuilder.put(AvailableMaterialsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (availableMaterialsViewModelProvider)));
      mapBuilder.put(CitizenDashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (citizenDashboardViewModelProvider)));
      mapBuilder.put(CitizenProfileViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (citizenProfileViewModelProvider)));
      mapBuilder.put(MaterialListViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (materialListViewModelProvider)));
      mapBuilder.put(RecyclerDashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (recyclerDashboardViewModelProvider)));
      mapBuilder.put(RecyclerProfileViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (recyclerProfileViewModelProvider)));
      return mapBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.addMaterialViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.authViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.availableMaterialsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.citizenDashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.citizenProfileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.materialListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.recyclerDashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.recyclerProfileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(hiltViewModelMapMapOfClassOfAndProviderOfViewModelBuilder());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.econect.app.presentation.citizen.material.AddMaterialViewModel
          return (T) new AddMaterialViewModel(singletonCImpl.userDataStoreProvider.get(), viewModelCImpl.addMaterialUseCase(), viewModelCImpl.getUserProfileUseCase(), singletonCImpl.provideMaterialClassifierProvider.get());

          case 1: // com.econect.app.presentation.auth.AuthViewModel
          return (T) new AuthViewModel(viewModelCImpl.loginUseCase(), viewModelCImpl.registerUseCase(), viewModelCImpl.logoutUseCase(), viewModelCImpl.getCurrentUserUseCase(), singletonCImpl.userDataStoreProvider.get());

          case 2: // com.econect.app.presentation.recycler.materials.AvailableMaterialsViewModel
          return (T) new AvailableMaterialsViewModel(singletonCImpl.bindMaterialRepositoryProvider.get(), singletonCImpl.userDataStoreProvider.get());

          case 3: // com.econect.app.presentation.citizen.dashboard.CitizenDashboardViewModel
          return (T) new CitizenDashboardViewModel(singletonCImpl.userDataStoreProvider.get(), viewModelCImpl.getUserProfileUseCase(), viewModelCImpl.getCitizenMaterialsUseCase(), singletonCImpl.bindMaterialRepositoryProvider.get(), viewModelCImpl.logoutUseCase());

          case 4: // com.econect.app.presentation.citizen.profile.CitizenProfileViewModel
          return (T) new CitizenProfileViewModel(singletonCImpl.userDataStoreProvider.get(), viewModelCImpl.getUserProfileUseCase(), viewModelCImpl.updateProfileUseCase(), viewModelCImpl.manageLocationsUseCase(), viewModelCImpl.manageSchedulesUseCase());

          case 5: // com.econect.app.presentation.citizen.material.MaterialListViewModel
          return (T) new MaterialListViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.userDataStoreProvider.get(), viewModelCImpl.getCitizenMaterialsUseCase(), singletonCImpl.bindMaterialRepositoryProvider.get());

          case 6: // com.econect.app.presentation.recycler.dashboard.RecyclerDashboardViewModel
          return (T) new RecyclerDashboardViewModel(singletonCImpl.userDataStoreProvider.get(), viewModelCImpl.getUserProfileUseCase(), viewModelCImpl.getRecyclerRoutesUseCase(), viewModelCImpl.getRecyclerTransactionsUseCase(), singletonCImpl.bindRouteRepositoryProvider.get(), singletonCImpl.bindTransactionRepositoryProvider.get(), singletonCImpl.bindMaterialRepositoryProvider.get(), viewModelCImpl.logoutUseCase(), singletonCImpl.bindLocationRepositoryProvider.get());

          case 7: // com.econect.app.presentation.recycler.profile.RecyclerProfileViewModel
          return (T) new RecyclerProfileViewModel(singletonCImpl.userDataStoreProvider.get(), viewModelCImpl.getUserProfileUseCase(), viewModelCImpl.updateProfileUseCase(), viewModelCImpl.manageSchedulesUseCase(), viewModelCImpl.getRecyclingCentersUseCase());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends EConectApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends EConectApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectEConectFcmService(EConectFcmService eConectFcmService) {
      injectEConectFcmService2(eConectFcmService);
    }

    @CanIgnoreReturnValue
    private EConectFcmService injectEConectFcmService2(EConectFcmService instance) {
      EConectFcmService_MembersInjector.injectAuth(instance, singletonCImpl.provideFirebaseAuthProvider.get());
      EConectFcmService_MembersInjector.injectFirestore(instance, singletonCImpl.provideFirebaseFirestoreProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends EConectApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    Provider<UserDataStore> userDataStoreProvider;

    Provider<EConectDatabase> provideDatabaseProvider;

    Provider<FirebaseFirestore> provideFirebaseFirestoreProvider;

    Provider<MaterialRepositoryImpl> materialRepositoryImplProvider;

    Provider<MaterialRepository> bindMaterialRepositoryProvider;

    Provider<UserRepositoryImpl> userRepositoryImplProvider;

    Provider<UserRepository> bindUserRepositoryProvider;

    Provider<MaterialClassifier> provideMaterialClassifierProvider;

    Provider<FirebaseAuth> provideFirebaseAuthProvider;

    Provider<AuthRepositoryImpl> authRepositoryImplProvider;

    Provider<AuthRepository> bindAuthRepositoryProvider;

    Provider<RouteRepositoryImpl> routeRepositoryImplProvider;

    Provider<RouteRepository> bindRouteRepositoryProvider;

    Provider<TransactionRepositoryImpl> transactionRepositoryImplProvider;

    Provider<TransactionRepository> bindTransactionRepositoryProvider;

    Provider<FusedLocationProviderClient> provideFusedLocationProviderClientProvider;

    Provider<RecyclingCenterRepositoryImpl> recyclingCenterRepositoryImplProvider;

    Provider<RecyclingCenterRepository> bindRecyclingCenterRepositoryProvider;

    Provider<LocationRepositoryImpl> locationRepositoryImplProvider;

    Provider<LocationRepository> bindLocationRepositoryProvider;

    SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    RecyclableMaterialDao recyclableMaterialDao() {
      return DatabaseModule_ProvideRecyclableMaterialDaoFactory.provideRecyclableMaterialDao(provideDatabaseProvider.get());
    }

    UserDao userDao() {
      return DatabaseModule_ProvideUserDaoFactory.provideUserDao(provideDatabaseProvider.get());
    }

    RouteDao routeDao() {
      return DatabaseModule_ProvideRouteDaoFactory.provideRouteDao(provideDatabaseProvider.get());
    }

    TransactionDao transactionDao() {
      return DatabaseModule_ProvideTransactionDaoFactory.provideTransactionDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.userDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<UserDataStore>(singletonCImpl, 0));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<EConectDatabase>(singletonCImpl, 2));
      this.provideFirebaseFirestoreProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseFirestore>(singletonCImpl, 3));
      this.materialRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 1);
      this.bindMaterialRepositoryProvider = DoubleCheck.provider((Provider) (materialRepositoryImplProvider));
      this.userRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 4);
      this.bindUserRepositoryProvider = DoubleCheck.provider((Provider) (userRepositoryImplProvider));
      this.provideMaterialClassifierProvider = DoubleCheck.provider(new SwitchingProvider<MaterialClassifier>(singletonCImpl, 5));
      this.provideFirebaseAuthProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseAuth>(singletonCImpl, 7));
      this.authRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 6);
      this.bindAuthRepositoryProvider = DoubleCheck.provider((Provider) (authRepositoryImplProvider));
      this.routeRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 8);
      this.bindRouteRepositoryProvider = DoubleCheck.provider((Provider) (routeRepositoryImplProvider));
      this.transactionRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 9);
      this.bindTransactionRepositoryProvider = DoubleCheck.provider((Provider) (transactionRepositoryImplProvider));
      this.provideFusedLocationProviderClientProvider = DoubleCheck.provider(new SwitchingProvider<FusedLocationProviderClient>(singletonCImpl, 11));
      this.recyclingCenterRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 12);
      this.bindRecyclingCenterRepositoryProvider = DoubleCheck.provider((Provider) (recyclingCenterRepositoryImplProvider));
      this.locationRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 10);
      this.bindLocationRepositoryProvider = DoubleCheck.provider((Provider) (locationRepositoryImplProvider));
    }

    @Override
    public void injectEConectApp(EConectApp eConectApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.econect.app.data.local.datastore.UserDataStore
          return (T) new UserDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.econect.app.data.repository.MaterialRepositoryImpl
          return (T) new MaterialRepositoryImpl(singletonCImpl.recyclableMaterialDao(), singletonCImpl.provideFirebaseFirestoreProvider.get());

          case 2: // com.econect.app.data.local.db.EConectDatabase
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.google.firebase.firestore.FirebaseFirestore
          return (T) FirebaseModule_ProvideFirebaseFirestoreFactory.provideFirebaseFirestore();

          case 4: // com.econect.app.data.repository.UserRepositoryImpl
          return (T) new UserRepositoryImpl(singletonCImpl.provideFirebaseFirestoreProvider.get(), singletonCImpl.userDao());

          case 5: // com.econect.app.ml.MaterialClassifier
          return (T) MlModule_ProvideMaterialClassifierFactory.provideMaterialClassifier(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // com.econect.app.data.repository.AuthRepositoryImpl
          return (T) new AuthRepositoryImpl(singletonCImpl.provideFirebaseAuthProvider.get(), singletonCImpl.provideFirebaseFirestoreProvider.get());

          case 7: // com.google.firebase.auth.FirebaseAuth
          return (T) FirebaseModule_ProvideFirebaseAuthFactory.provideFirebaseAuth();

          case 8: // com.econect.app.data.repository.RouteRepositoryImpl
          return (T) new RouteRepositoryImpl(singletonCImpl.routeDao(), singletonCImpl.provideFirebaseFirestoreProvider.get());

          case 9: // com.econect.app.data.repository.TransactionRepositoryImpl
          return (T) new TransactionRepositoryImpl(singletonCImpl.transactionDao(), singletonCImpl.provideFirebaseFirestoreProvider.get());

          case 10: // com.econect.app.data.repository.LocationRepositoryImpl
          return (T) new LocationRepositoryImpl(singletonCImpl.provideFusedLocationProviderClientProvider.get(), singletonCImpl.bindRecyclingCenterRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 11: // com.google.android.gms.location.FusedLocationProviderClient
          return (T) LocationModule_Companion_ProvideFusedLocationProviderClientFactory.provideFusedLocationProviderClient(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 12: // com.econect.app.data.repository.RecyclingCenterRepositoryImpl
          return (T) new RecyclingCenterRepositoryImpl(singletonCImpl.provideFirebaseFirestoreProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
