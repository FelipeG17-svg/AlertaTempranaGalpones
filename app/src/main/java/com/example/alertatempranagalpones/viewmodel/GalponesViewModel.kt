package com.example.alertatempranagalpones.viewmodel

//import androidx.lifecycle.ViewModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertatempranagalpones.data.local.AppDatabase
import com.example.alertatempranagalpones.data.model.Galpon
import com.example.alertatempranagalpones.data.repository.GalponRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class GalponesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppDatabase.obtener(application).let { db ->
        GalponRepository(db.galponDao(), db.medicionDao())
    }

    val galpones: StateFlow<List<Galpon>> = repository.galpones.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        cargarGalpones()
    }

    fun cargarGalpones() {
        viewModelScope.launch {
            _cargando.value = true
            repository.sincronizar()
            _cargando.value = false
        }
    }
}


/* class GalponesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GalponRepository(
        AppDatabase.obtener(application).galponDao()
    )


    val galpones: StateFlow<List<Galpon>> = repository.galpones.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        cargarGalpones()
    }

    fun cargarGalpones() {
        viewModelScope.launch {
            _cargando.value = true
            repository.sincronizar()
            _cargando.value = false
        }
    }
} */




/* class GalponesViewModel : ViewModel() {

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
} */