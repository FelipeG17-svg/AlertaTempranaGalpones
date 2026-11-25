package com.example.alertatempranagalpones.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alertatempranagalpones.ui.screens.DetalleGalponScreen
import com.example.alertatempranagalpones.ui.screens.ListaGalponesScreen
import com.example.alertatempranagalpones.ui.screens.LoginScreen
import com.example.alertatempranagalpones.ui.screens.FormularioGalponScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN,
        enterTransition = { slideInHorizontally(tween(350)) { it / 3 } + fadeIn(tween(350)) },
        exitTransition = { slideOutHorizontally(tween(350)) { -it / 3 } + fadeOut(tween(350)) },
        popEnterTransition = { slideInHorizontally(tween(350)) { -it / 3 } + fadeIn(tween(350)) },
        popExitTransition = { slideOutHorizontally(tween(350)) { it / 3 } + fadeOut(tween(350)) }
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
                },
                onNuevoGalpon = {
                    navController.navigate(Rutas.formularioGalpon())
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
        composable(
            route = Rutas.FORMULARIO_GALPON,
            arguments = listOf(navArgument("galponId") { type = NavType.LongType })
        ) { backStackEntry ->
            val galponId = backStackEntry.arguments?.getLong("galponId") ?: 0L
            FormularioGalponScreen(
                galponId = galponId,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}




/* @Composable
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
} */
