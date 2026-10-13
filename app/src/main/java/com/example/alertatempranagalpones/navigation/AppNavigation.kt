package com.example.alertatempranagalpones.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alertatempranagalpones.ui.screens.DetalleGalponScreen
import com.example.alertatempranagalpones.ui.screens.ListaGalponesScreen
import com.example.alertatempranagalpones.ui.screens.LoginScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onIngresar = {
                    navController.navigate(Rutas.LISTA_GALPONES) {
                        // Quita el login de la pila: al volver atrás no regresa al login
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.LISTA_GALPONES) {
            ListaGalponesScreen(
                onGalponClick = { galponId ->
                    navController.navigate(Rutas.detalleGalpon(galponId))
                }
            )
        }

        composable(
            route = Rutas.DETALLE_GALPON,
            arguments = listOf(navArgument("galponId") { type = NavType.LongType })
        ) { backStackEntry ->
            val galponId = backStackEntry.arguments?.getLong("galponId") ?: 0L
            DetalleGalponScreen(
                galponId = galponId,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
