package com.example.alertatempranagalpones.navigation

/** Nombres de las rutas de la app. Se definen en un solo lugar para evitar errores de tipeo. */
object Rutas {
    const val LOGIN = "login"
    const val LISTA_GALPONES = "galpones"
    const val DETALLE_GALPON = "galpon/{galponId}"

    fun detalleGalpon(galponId: Long) = "galpon/$galponId"

    const val FORMULARIO_GALPON = "formulario/{galponId}"

    // galponId = 0 significa "galpón nuevo"
    fun formularioGalpon(galponId: Long = 0L) = "formulario/$galponId"
}