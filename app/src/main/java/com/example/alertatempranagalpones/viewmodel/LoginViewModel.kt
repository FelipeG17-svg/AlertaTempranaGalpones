package com.example.alertatempranagalpones.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val errorCorreo: String? = null,
    val errorClave: String? = null,
    val claveVisible: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChange(valor: String) {
        _uiState.update {
            // Valida mientras escribe, pero no molesta con un error si el campo está vacío
            it.copy(correo = valor, errorCorreo = if (valor.isBlank()) null else validarCorreo(valor))
        }
    }

    fun onClaveChange(valor: String) {
        _uiState.update {
            it.copy(clave = valor, errorClave = if (valor.isBlank()) null else validarClave(valor))
        }
    }

    fun alternarVisibilidadClave() {
        _uiState.update { it.copy(claveVisible = !it.claveVisible) }
    }

    fun validarFormulario(): Boolean {
        val actual = _uiState.value
        val errorCorreo = validarCorreo(actual.correo)
        val errorClave = validarClave(actual.clave)
        _uiState.update { it.copy(errorCorreo = errorCorreo, errorClave = errorClave) }
        return errorCorreo == null && errorClave == null
    }

    private fun validarCorreo(correo: String): String? = when {
        correo.isBlank() -> "El correo es obligatorio"
        !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> "Ingresa un correo válido"
        else -> null
    }

    private fun validarClave(clave: String): String? = when {
        clave.isBlank() -> "La contraseña es obligatoria"
        clave.length < 6 -> "Debe tener al menos 6 caracteres"
        else -> null
    }
}