package com.example.alertatempranagalpones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertatempranagalpones.data.model.Galpon
import com.example.alertatempranagalpones.data.repository.GalponRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Mantiene el estado de la lista de galpones. La pantalla solo lo observa. */
class GalponesViewModel : ViewModel() {

    private val repository = GalponRepository()

    private val _galpones = MutableStateFlow<List<Galpon>>(emptyList())
    val galpones: StateFlow<List<Galpon>> = _galpones.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        cargarGalpones()
    }

    fun cargarGalpones() {
        viewModelScope.launch {
            _cargando.value = true
            _galpones.value = repository.obtenerGalpones()
            _cargando.value = false
        }
    }
}