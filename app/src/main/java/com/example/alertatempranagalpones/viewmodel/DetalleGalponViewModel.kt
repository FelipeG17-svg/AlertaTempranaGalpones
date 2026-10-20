package com.example.alertatempranagalpones.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.alertatempranagalpones.data.local.AppDatabase
import com.example.alertatempranagalpones.data.model.Galpon
import com.example.alertatempranagalpones.data.model.Medicion
import com.example.alertatempranagalpones.data.repository.GalponRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetalleGalponViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val galponId: Long = savedStateHandle.get<Long>("galponId") ?: 0L

    private val repository = AppDatabase.obtener(application).let { db ->
        GalponRepository(db.galponDao(), db.medicionDao())
    }

    private val _galpon = MutableStateFlow<Galpon?>(null)
    val galpon: StateFlow<Galpon?> = _galpon.asStateFlow()

    val mediciones: StateFlow<List<Medicion>> = repository.observarMediciones(galponId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            _galpon.value = repository.obtenerGalpon(galponId)
        }
    }
}