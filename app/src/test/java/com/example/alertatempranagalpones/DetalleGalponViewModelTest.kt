package com.example.alertatempranagalpones

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.example.alertatempranagalpones.data.model.Galpon
import com.example.alertatempranagalpones.data.repository.GalponRepository
import com.example.alertatempranagalpones.viewmodel.DetalleGalponViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetalleGalponViewModelTest {

    private val repo = mockk<GalponRepository>()
    private val app = mockk<Application>(relaxed = true)
    private val estado = mockk<SavedStateHandle>()

    private val galpon = Galpon(
        id = 3,
        granja = "Granja Prueba",
        nombre = "Galpon 3",
        temperaturaActual = 33.8,
        humedadActual = 49.0,
        ultimaActualizacion = 0L
    )

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { estado.get<Long>("galponId") } returns 3L
        every { repo.observarMediciones(3L) } returns flowOf(emptyList())
        every { repo.observarAlertas(3L) } returns flowOf(emptyList())
        coEvery { repo.obtenerGalpon(3L) } returns galpon
        coEvery { repo.registrarAccion(any(), any()) } returns Unit
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun crearViewModel() = DetalleGalponViewModel(app, estado, repo)

    @Test
    fun alIniciarCargaElGalponDelId() {
        val vm = crearViewModel()
        assertEquals(galpon, vm.galpon.value)
    }

    @Test
    fun accionMuyCortaDevuelveError() {
        val vm = crearViewModel()
        val error = vm.registrarAccion(7L, "abc")
        assertEquals("Describe la acción realizada (mínimo 5 caracteres)", error)
        coVerify(exactly = 0) { repo.registrarAccion(any(), any()) }
    }

    @Test
    fun accionSoloConEspaciosDevuelveError() {
        val vm = crearViewModel()
        val error = vm.registrarAccion(7L, "      ")
        assertEquals("Describe la acción realizada (mínimo 5 caracteres)", error)
    }

    @Test
    fun accionMuyLargaDevuelveError() {
        val vm = crearViewModel()
        val error = vm.registrarAccion(7L, "a".repeat(201))
        assertEquals("La descripción es muy larga (máximo 200 caracteres)", error)
        coVerify(exactly = 0) { repo.registrarAccion(any(), any()) }
    }

    @Test
    fun accionValidaSeGuardaYNoDevuelveError() {
        val vm = crearViewModel()
        val error = vm.registrarAccion(7L, "  Se encendieron los ventiladores  ")
        assertNull(error)
        coVerify { repo.registrarAccion(7L, "Se encendieron los ventiladores") }
    }

    @Test
    fun accionConElMinimoDeCaracteresEsValida() {
        val vm = crearViewModel()
        assertNull(vm.registrarAccion(7L, "12345"))
    }
}