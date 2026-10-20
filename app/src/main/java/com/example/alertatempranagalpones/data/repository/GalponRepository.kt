package com.example.alertatempranagalpones.data.repository

import com.example.alertatempranagalpones.data.local.GalponDao
import com.example.alertatempranagalpones.data.local.MedicionDao
import com.example.alertatempranagalpones.data.model.Galpon
import com.example.alertatempranagalpones.data.model.Medicion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class GalponRepository(
    private val galponDao: GalponDao,
    private val medicionDao: MedicionDao
) {

    val galpones: Flow<List<Galpon>> = galponDao.observarTodos()

    suspend fun obtenerGalpon(id: Long): Galpon? = galponDao.obtenerPorId(id)

    fun observarMediciones(galponId: Long): Flow<List<Medicion>> =
        medicionDao.observarPorGalpon(galponId)

    suspend fun sincronizar() {
        delay(800) // simula la espera de una consulta a un servicio
        val simulados = datosSimulados()
        if (galponDao.contar() == 0) {
            galponDao.guardarTodos(simulados)
        }
        if (medicionDao.contar() == 0) {
            medicionDao.insertarTodas(historialSimulado(simulados))
        }
    }

    private fun datosSimulados(): List<Galpon> {
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

    private fun historialSimulado(galpones: List<Galpon>): List<Medicion> =
        galpones.flatMap { galpon ->
            (0 until 12).map { i ->
                val variacionTemp = if (i == 0) 0.0 else ((i * 7 + galpon.id.toInt() * 3) % 5 - 2) * 0.4
                val variacionHum = if (i == 0) 0.0 else ((i * 5 + galpon.id.toInt()) % 5 - 2) * 1.2
                Medicion(
                    galponId = galpon.id,
                    temperatura = galpon.temperaturaActual + variacionTemp,
                    humedad = galpon.humedadActual + variacionHum,
                    fechaHora = galpon.ultimaActualizacion - i * 30L * 60L * 1000L
                )
            }
        }
}


/* class GalponRepository(private val galponDao: GalponDao) {

    val galpones: Flow<List<Galpon>> = galponDao.observarTodos()

    suspend fun sincronizar() {
        delay(800)
        if (galponDao.contar() == 0) {
            galponDao.guardarTodos(datosSimulados())
        }
    }

    private fun datosSimulados(): List<Galpon> {
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
} */


/* class GalponRepository {

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
} */