package com.example.alertatempranagalpones.viewmodel

//import androidx.lifecycle.ViewModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertatempranagalpones.data.remote.ClimaActual
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
        GalponRepository(db.galponDao(), db.medicionDao(), db.alertaDao())
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
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    private val _clima = MutableStateFlow<ClimaActual?>(null)
    val clima: StateFlow<ClimaActual?> = _clima.asStateFlow()

    fun cargarClima() {
        viewModelScope.launch {
            repository.obtenerClima().onSuccess { _clima.value = it }
        }
    }

    init {
        cargarClima()
    }

    fun crearGalpon(
        granja: String,
        nombre: String,
        temperatura: Double,
        humedad: Double,
        alTerminar: () -> Unit
    ) {
        viewModelScope.launch {
            val nuevo = Galpon(
                id = 0,
                granja = granja,
                nombre = nombre,
                temperaturaActual = temperatura,
                humedadActual = humedad,
                ultimaActualizacion = System.currentTimeMillis()
            )
            repository.crearGalpon(nuevo)
                .onSuccess {
                    _mensaje.value = "Galpón creado"
                    alTerminar()
                }
                .onFailure { _mensaje.value = "No se pudo crear: revisa la conexión con el servidor" }
        }
    }

    fun actualizarGalpon(galpon: Galpon, alTerminar: () -> Unit) {
        viewModelScope.launch {
            repository.actualizarGalpon(galpon)
                .onSuccess {
                    _mensaje.value = "Galpón actualizado"
                    alTerminar()
                }
                .onFailure { _mensaje.value = "No se pudo actualizar: revisa la conexión con el servidor" }
        }
    }

    fun borrarGalpon(id: Long, alTerminar: () -> Unit = {}) {
        viewModelScope.launch {
            repository.borrarGalpon(id)
                .onSuccess {
                    _mensaje.value = "Galpón borrado"
                    alTerminar()
                }
                .onFailure { _mensaje.value = "No se pudo borrar: revisa la conexión con el servidor" }
        }
    }


}


/* class GalponesViewModel(application: Application) : AndroidViewModel(application) {

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
} */
