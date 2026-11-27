package com.example.alertatempranagalpones

import com.example.alertatempranagalpones.viewmodel.LoginViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginViewModelTest {

    @Test
    fun correoVacioNoMuestraErrorMientrasSeEscribe() {
        val vm = LoginViewModel()
        vm.onCorreoChange("")
        assertNull(vm.uiState.value.errorCorreo)
    }

    @Test
    fun claveCortaMuestraError() {
        val vm = LoginViewModel()
        vm.onClaveChange("123")
        assertEquals("Debe tener al menos 6 caracteres", vm.uiState.value.errorClave)
    }

    @Test
    fun claveDeSeisCaracteresEsValida() {
        val vm = LoginViewModel()
        vm.onClaveChange("123456")
        assertNull(vm.uiState.value.errorClave)
        assertEquals("123456", vm.uiState.value.clave)
    }

    @Test
    fun alternarVisibilidadCambiaElEstado() {
        val vm = LoginViewModel()
        assertFalse(vm.uiState.value.claveVisible)
        vm.alternarVisibilidadClave()
        assertTrue(vm.uiState.value.claveVisible)
        vm.alternarVisibilidadClave()
        assertFalse(vm.uiState.value.claveVisible)
    }

    @Test
    fun formularioVacioNoEsValidoYMuestraErrores() {
        val vm = LoginViewModel()
        val valido = vm.validarFormulario()
        assertFalse(valido)
        assertEquals("El correo es obligatorio", vm.uiState.value.errorCorreo)
        assertEquals("La contraseña es obligatoria", vm.uiState.value.errorClave)
    }
}