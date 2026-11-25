package com.example.alertatempranagalpones.data.repository

import com.example.alertatempranagalpones.data.remote.ClimaActual
import com.example.alertatempranagalpones.data.remote.ClimaApi
import com.example.alertatempranagalpones.data.local.AlertaDao
import com.example.alertatempranagalpones.data.local.GalponDao
import com.example.alertatempranagalpones.data.local.MedicionDao
import com.example.alertatempranagalpones.data.model.Alerta
import com.example.alertatempranagalpones.data.model.EstadoGalpon
import com.example.alertatempranagalpones.data.model.Galpon
import com.example.alertatempranagalpones.data.model.Medicion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import com.example.alertatempranagalpones.data.remote.GalponApi
import com.example.alertatempranagalpones.data.remote.RetrofitClient

class GalponRepository(
    private val galponDao: GalponDao,
    private val medicionDao: MedicionDao,
    private val alertaDao: AlertaDao,
    private val api: GalponApi = RetrofitClient.galponApi,
    private val climaApi: ClimaApi = RetrofitClient.climaApi
) {

    val galpones: Flow<List<Galpon>> = galponDao.observarTodos()

    suspend fun obtenerGalpon(id: Long): Galpon? = galponDao.obtenerPorId(id)

    fun observarMediciones(galponId: Long): Flow<List<Medicion>> =
        medicionDao.observarPorGalpon(galponId)

    fun observarAlertas(galponId: Long): Flow<List<Alerta>> =
        alertaDao.observarPorGalpon(galponId)

    suspend fun registrarAccion(alertaId: Long, accion: String) =
        alertaDao.registrarAccion(alertaId, accion)
    suspend fun sincronizar(): Boolean {
        // 1. Intenta traer los galpones del backend
        val conectado = try {
            val remotos = api.listar()
            galponDao.guardarTodos(remotos)
            galponDao.borrarQueNoEstenEn(remotos.map { it.id })
            true
        } catch (e: Exception) {
            false
        }

        // 2. Medidas y alertas siguen siendo locales por ahora
        val simulados = datosSimulados()
        if (galponDao.contar() == 0) {
            galponDao.guardarTodos(simulados)
        }
        if (medicionDao.contar() == 0) {
            medicionDao.insertarTodas(historialSimulado(simulados))
        }
        if (alertaDao.contar() == 0) {
            alertasSimuladas(simulados).forEach { alertaDao.insertar(it) }
        }
        return conectado
    }

    suspend fun crearGalpon(galpon: Galpon): Result<Galpon> = runCatching {
        val creado = api.crear(galpon.copy(id = 0))
        galponDao.guardar(creado)
        creado
    }

    suspend fun actualizarGalpon(galpon: Galpon): Result<Galpon> = runCatching {
        val actualizado = api.actualizar(galpon.id, galpon)
        galponDao.guardar(actualizado)
        actualizado
    }

    suspend fun borrarGalpon(id: Long): Result<Unit> = runCatching {
        val respuesta = api.borrar(id)
        if (!respuesta.isSuccessful) error("Error ${respuesta.code()}")
        galponDao.borrarPorId(id)
    }

    // Clima exterior de Melipilla (API externa Open-Meteo)
    suspend fun obtenerClima(): Result<ClimaActual> = runCatching {
        climaApi.obtenerClima(latitud = -33.6833, longitud = -71.2167).current
            ?: error("Sin datos de clima")
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

    private fun alertasSimuladas(galpones: List<Galpon>): List<Alerta> {
        val pendientes = galpones
            .filter { it.estado != EstadoGalpon.NORMAL }
            .map { galpon ->
                val umbral = if (galpon.estado == EstadoGalpon.CRITICO) {
                    "crítico (%.0f °C)".format(EstadoGalpon.UMBRAL_CRITICO)
                } else {
                    "de advertencia (%.0f °C)".format(EstadoGalpon.UMBRAL_ADVERTENCIA)
                }
                Alerta(
                    galponId = galpon.id,
                    estado = galpon.estado,
                    mensaje = "Temperatura de %.1f °C sobre el umbral %s".format(galpon.temperaturaActual, umbral),
                    fechaHora = galpon.ultimaActualizacion
                )
            }

        val antigua = Alerta(
            galponId = 4,
            estado = EstadoGalpon.ADVERTENCIA,
            mensaje = "Temperatura de 28.4 °C sobre el umbral de advertencia (28 °C)",
            fechaHora = galpones.first().ultimaActualizacion - 26L * 60L * 60L * 1000L,
            atendida = true,
            accionRealizada = "Se encendieron los ventiladores adicionales"
        )

        return pendientes + antigua
    }
}


/* class GalponRepository(
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
} */