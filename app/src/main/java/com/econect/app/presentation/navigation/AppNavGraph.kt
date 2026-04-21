package com.econect.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.econect.app.domain.model.UserType
import com.econect.app.presentation.auth.LoginScreen
import com.econect.app.presentation.auth.RegisterScreen
import com.econect.app.presentation.citizen.dashboard.CitizenDashboardScreen
import com.econect.app.presentation.citizen.material.AddMaterialScreen
import com.econect.app.presentation.recycler.dashboard.RecyclerDashboardScreen
import com.econect.app.presentation.citizen.material.MaterialListScreen
import com.econect.app.presentation.citizen.profile.CitizenProfileScreen
import com.econect.app.presentation.recycler.profile.RecyclerProfileScreen
import com.econect.app.presentation.recycler.materials.AvailableMaterialsScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.LOGIN
    ) {
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.REGISTER)
                },
                onLoginSuccess = { userType ->
                    val destination = if (userType == UserType.CITIZEN) {
                        NavRoutes.CITIZEN_HOME
                    } else {
                        NavRoutes.RECYCLER_HOME
                    }
                    navController.navigate(destination) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.AVAILABLE_MATERIALS) {
            AvailableMaterialsScreen()
        }
        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = { userType ->
                    val destination = if (userType == UserType.CITIZEN) {
                        NavRoutes.CITIZEN_HOME
                    } else {
                        NavRoutes.RECYCLER_HOME
                    }
                    navController.navigate(destination) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.CITIZEN_HOME) {
            CitizenDashboardScreen(
                onNavigateToAddMaterial = {
                    navController.navigate(NavRoutes.ADD_MATERIAL)
                },
                onNavigateToMaterialList = {
                    navController.navigate(NavRoutes.MATERIAL_LIST)
                },
                onNavigateToChat = { routeId ->
                    // TODO: navegar a ChatScreen(routeId) cuando esté disponible
                }
            )
        }

        composable(NavRoutes.CITIZEN_PROFILE) {
            CitizenProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ADD_MATERIAL) {
            AddMaterialScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.RECYCLER_HOME) {
            RecyclerDashboardScreen(
                onNavigateToRouteDetail = { routeId ->
                    navController.navigate(NavRoutes.routeDetail(routeId))
                },
                onNavigateToAvailableMaterials = {
                    navController.navigate(NavRoutes.AVAILABLE_MATERIALS)
                }
            )
        }
        composable(NavRoutes.MATERIAL_LIST) {
            MaterialListScreen()
        }

        composable(NavRoutes.RECYCLER_PROFILE) {
            RecyclerProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }



        // Placeholder: se reemplazará con RouteDetailScreen cuando esté disponible
        composable(NavRoutes.ROUTE_DETAIL) {
            val routeId = it.arguments?.getString("routeId") ?: ""
            Text(
                text = "Detalle de ruta $routeId — próximamente",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
