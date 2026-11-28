package com.example.alertatempranagalpones

import android.app.Application
import com.example.alertatempranagalpones.data.local.AlertaDao
import com.example.alertatempranagalpones.data.local.GalponDao
import com.example.alertatempranagalpones.data.local.MedicionDao
import com.example.alertatempranagalpones.data.model.Galpon
import com.example.alertatempranagalpones.data.remote.ClimaActual
import com.example.alertatempranagalpones.data.remote.ClimaApi
import com.example.alertatempranagalpones.data.remote.ClimaRespuesta
import com.example.alertatempranagalpones.data.remote.GalponApi
import com.example.alertatempranagalpones.data.repository.GalponRepository
import com.example.alertatempranagalpones.viewmodel.GalponesViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class GalponesViewModelTest {

    private val galponDao = mockk<GalponDao>(relaxed = true)
    private val medicionDao = mockk<MedicionDao>(relaxed = true)
    private val alertaDao = mockk<AlertaDao>(relaxed = true)
    private val api = mockk<GalponApi>()
    private val climaApi = mockk<ClimaApi>()
    private val app = mockk<Application>(relaxed = true)

    private val galpon = Galpon(
        id = 1,
        granja = "Granja Prueba",
        nombre = "Galpon 1",
        temperaturaActual = 25.0,
        humedadActual = 50.0,
        ultimaActualizacion = 0L
    )

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        coEvery { api.listar() } returns emptyList()
        coEvery { climaApi.obtenerClima(any(), any(), any()) } returns
                ClimaRespuesta(current = ClimaActual(temperatura = 20.0, humedad = 45.0))
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun crearViewModel(): GalponesViewModel {
        val repo = GalponRepository(galponDao, medicionDao, alertaDao, api, climaApi)
        return GalponesViewModel(app, repo)
    }

    @Test
    fun alIniciarTerminaDeCargar() {
        val vm = crearViewModel()
        assertFalse(vm.cargando.value)
    }

    @Test
    fun alIniciarCargaElClima() {
        val vm = crearViewModel()
        assertEquals(20.0, vm.clima.value?.temperatura ?: 0.0, 0.001)
    }

    @Test
    fun siElClimaFallaQuedaVacio() {
        coEvery { climaApi.obtenerClima(any(), any(), any()) } throws IOException("sin internet")
        val vm = crearViewModel()
        assertNull(vm.clima.value)
    }

    @Test
    fun crearGalponConExitoAvisaYTermina() {
        coEvery { api.crear(any()) } returns galpon
        val vm = crearViewModel()
        var termino = false

        vm.crearGalpon("Granja Prueba", "Galpon 1", 25.0, 50.0) { termino = true }

        assertTrue(termino)
        assertEquals("Galpón creado", vm.mensaje.value)
    }

    @Test
    fun crearGalponConErrorMuestraMensajeYNoTermina() {
        coEvery { api.crear(any()) } throws IOException("sin conexion")
        val vm = crearViewModel()
        var termino = false

        vm.crearGalpon("Granja Prueba", "Galpon 1", 25.0, 50.0) { termino = true }

        assertFalse(termino)
        assertTrue(vm.mensaje.value!!.startsWith("No se pudo crear"))
    }

    @Test
    fun actualizarGalponConExitoAvisaYTermina() {
        coEvery { api.actualizar(1L, any()) } returns galpon
        val vm = crearViewModel()
        var termino = false

        vm.actualizarGalpon(galpon) { termino = true }

        assertTrue(termino)
        assertEquals("Galpón actualizado", vm.mensaje.value)
    }

    @Test
    fun actualizarGalponConErrorMuestraMensaje() {
        coEvery { api.actualizar(any(), any()) } throws IOException("sin conexion")
        val vm = crearViewModel()
        var termino = false

        vm.actualizarGalpon(galpon) { termino = true }

        assertFalse(termino)
        assertTrue(vm.mensaje.value!!.startsWith("No se pudo actualizar"))
    }

    @Test
    fun borrarGalponConExitoAvisaYTermina() {
        coEvery { api.borrar(1L) } returns Response.success(Unit)
        val vm = crearViewModel()
        var termino = false

        vm.borrarGalpon(1L) { termino = true }

        assertTrue(termino)
        assertEquals("Galpón borrado", vm.mensaje.value)
    }

    @Test
    fun borrarGalponConErrorMuestraMensaje() {
        coEvery { api.borrar(any()) } throws IOException("sin conexion")
        val vm = crearViewModel()
        var termino = false

        vm.borrarGalpon(1L) { termino = true }

        assertFalse(termino)
        assertTrue(vm.mensaje.value!!.startsWith("No se pudo borrar"))
    }

    @Test
    fun limpiarMensajeVaciaElMensaje() {
        coEvery { api.borrar(any()) } returns Response.success(Unit)
        val vm = crearViewModel()
        vm.borrarGalpon(1L)
        assertEquals("Galpón borrado", vm.mensaje.value)

        vm.limpiarMensaje()

        assertNull(vm.mensaje.value)
    }
}