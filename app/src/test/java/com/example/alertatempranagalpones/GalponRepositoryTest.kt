package com.example.alertatempranagalpones

import com.example.alertatempranagalpones.data.local.AlertaDao
import com.example.alertatempranagalpones.data.local.GalponDao
import com.example.alertatempranagalpones.data.local.MedicionDao
import com.example.alertatempranagalpones.data.model.Galpon
import com.example.alertatempranagalpones.data.remote.ClimaActual
import com.example.alertatempranagalpones.data.remote.ClimaApi
import com.example.alertatempranagalpones.data.remote.ClimaRespuesta
import com.example.alertatempranagalpones.data.remote.GalponApi
import com.example.alertatempranagalpones.data.repository.GalponRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class GalponRepositoryTest {

    private val galponDao = mockk<GalponDao>(relaxed = true)
    private val medicionDao = mockk<MedicionDao>(relaxed = true)
    private val alertaDao = mockk<AlertaDao>(relaxed = true)
    private val api = mockk<GalponApi>()
    private val climaApi = mockk<ClimaApi>()

    private val repo = GalponRepository(galponDao, medicionDao, alertaDao, api, climaApi)

    private fun galpon(id: Long, temperatura: Double) = Galpon(
        id = id,
        granja = "Granja Prueba",
        nombre = "Galpon $id",
        temperaturaActual = temperatura,
        humedadActual = 50.0,
        ultimaActualizacion = 0L
    )

    // ---------- sincronizar ----------

    @Test
    fun sincronizarConBackendGuardaLosGalponesRemotos() = runTest {
        val remotos = listOf(galpon(1, 24.0), galpon(2, 30.0))
        coEvery { api.listar() } returns remotos

        val conectado = repo.sincronizar()

        assertTrue(conectado)
        coVerify { galponDao.guardarTodos(remotos) }
        coVerify { galponDao.borrarQueNoEstenEn(listOf(1L, 2L)) }
    }

    @Test
    fun sincronizarSinBackendDevuelveFalseYNoBorraNada() = runTest {
        coEvery { api.listar() } throws IOException("sin conexion")

        val conectado = repo.sincronizar()

        assertFalse(conectado)
        coVerify(exactly = 0) { galponDao.borrarQueNoEstenEn(any()) }
        // la base local vacia se llena con los datos de ejemplo
        coVerify { galponDao.guardarTodos(any()) }
    }

    @Test
    fun sincronizarCargaMedicionesYAlertasLocales() = runTest {
        coEvery { api.listar() } returns emptyList()

        repo.sincronizar()

        coVerify { medicionDao.insertarTodas(match { it.size == 72 }) }
        coVerify(atLeast = 1) { alertaDao.insertar(any()) }
    }

    // ---------- crear ----------

    @Test
    fun crearGalponGuardaElGalponDevueltoPorElServidor() = runTest {
        val creado = galpon(9, 35.0)
        coEvery { api.crear(any()) } returns creado

        val resultado = repo.crearGalpon(galpon(0, 35.0))

        assertTrue(resultado.isSuccess)
        assertEquals(creado, resultado.getOrNull())
        coVerify { galponDao.guardar(creado) }
    }

    @Test
    fun crearGalponFallaSiElServidorNoResponde() = runTest {
        coEvery { api.crear(any()) } throws IOException("sin conexion")

        val resultado = repo.crearGalpon(galpon(0, 35.0))

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) { galponDao.guardar(any()) }
    }

    // ---------- actualizar ----------

    @Test
    fun actualizarGalponGuardaElGalponActualizado() = runTest {
        val actualizado = galpon(3, 25.0)
        coEvery { api.actualizar(3L, any()) } returns actualizado

        val resultado = repo.actualizarGalpon(galpon(3, 25.0))

        assertTrue(resultado.isSuccess)
        coVerify { galponDao.guardar(actualizado) }
    }

    @Test
    fun actualizarGalponFallaSiElServidorNoResponde() = runTest {
        coEvery { api.actualizar(any(), any()) } throws IOException("sin conexion")

        val resultado = repo.actualizarGalpon(galpon(3, 25.0))

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) { galponDao.guardar(any()) }
    }

    // ---------- borrar ----------

    @Test
    fun borrarGalponBorraTambienEnLaBaseLocal() = runTest {
        coEvery { api.borrar(5L) } returns Response.success(Unit)

        val resultado = repo.borrarGalpon(5L)

        assertTrue(resultado.isSuccess)
        coVerify { galponDao.borrarPorId(5L) }
    }

    @Test
    fun borrarGalponFallaConError404ySinTocarLaBaseLocal() = runTest {
        coEvery { api.borrar(99L) } returns Response.error(404, "".toResponseBody(null))

        val resultado = repo.borrarGalpon(99L)

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) { galponDao.borrarPorId(any()) }
    }

    @Test
    fun borrarGalponFallaSiNoHayConexion() = runTest {
        coEvery { api.borrar(any()) } throws IOException("sin conexion")

        val resultado = repo.borrarGalpon(5L)

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) { galponDao.borrarPorId(any()) }
    }

    // ---------- clima ----------

    @Test
    fun obtenerClimaDevuelveLaTemperaturaYHumedad() = runTest {
        coEvery { climaApi.obtenerClima(any(), any(), any()) } returns
                ClimaRespuesta(current = ClimaActual(temperatura = 21.5, humedad = 40.0))

        val resultado = repo.obtenerClima()

        assertTrue(resultado.isSuccess)
        assertEquals(21.5, resultado.getOrNull()?.temperatura ?: 0.0, 0.001)
    }

    @Test
    fun obtenerClimaFallaSiLaRespuestaNoTraeDatos() = runTest {
        coEvery { climaApi.obtenerClima(any(), any(), any()) } returns ClimaRespuesta(current = null)

        assertTrue(repo.obtenerClima().isFailure)
    }

    @Test
    fun obtenerClimaFallaSiNoHayInternet() = runTest {
        coEvery { climaApi.obtenerClima(any(), any(), any()) } throws IOException("sin internet")

        assertTrue(repo.obtenerClima().isFailure)
    }
}