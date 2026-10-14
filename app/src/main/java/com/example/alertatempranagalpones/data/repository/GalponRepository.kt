package com.example.alertatempranagalpones.data.repository

import com.example.alertatempranagalpones.data.model.Galpon
import kotlinx.coroutines.delay

class GalponRepository {

    suspend fun obtenerGalpones(): List<Galpon> {
        delay(800) // simula la espera de una consulta a un servicio
        val ahora = System.currentTimeMillis()
        return listOf(
            Galpon(id = 1, granja = "Granja Melipilla", nombre = "Galpón 1", temperaturaActual = 24.5, humedadActual = 62.0, ultimaActualizacion = ahora),
            Galpon(id = 2, granja = "Granja Melipilla", nombre = "Galpón 2", temperaturaActual = 29.3, humedadActual = 58.5, ultimaActualizacion = ahora),
            Galpon(id = 3, granja = "Granja Melipilla", nombre = "Galpón 3", temperaturaActual = 33.8, humedadActual = 49.0, ultimaActualizacion = ahora),
            Galpon(id = 4, granja = "Granja Talagante", nombre = "Galpón 1", temperaturaActual = 26.1, humedadActual = 65.2, ultimaActualizacion = ahora),
            Galpon(id = 5, granja = "Granja Talagante", nombre = "Galpón 2", temperaturaActual = 31.0, humedadActual = 54.7, ultimaActualizacion = ahora),
            Galpon(id = 6, granja = "Granja Talagante", nombre = "Galpón 3", temperaturaActual = 22.9, humedadActual = 70.3, ultimaActualizacion = ahora)
        )
    }
}