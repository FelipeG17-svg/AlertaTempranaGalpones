package com.example.alertatempranagalpones.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alertatempranagalpones.viewmodel.GalponesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioGalponScreen(
    galponId: Long,
    onVolver: () -> Unit,
    viewModel: GalponesViewModel = viewModel()
) {
    val galpones by viewModel.galpones.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val esEdicion = galponId != 0L
    val existente = galpones.firstOrNull { it.id == galponId }

    var granja by rememberSaveable(existente?.id) { mutableStateOf(existente?.granja ?: "") }
    var nombre by rememberSaveable(existente?.id) { mutableStateOf(existente?.nombre ?: "") }
    var temperatura by rememberSaveable(existente?.id) {
        mutableStateOf(existente?.temperaturaActual?.toString() ?: "")
    }
    var humedad by rememberSaveable(existente?.id) {
        mutableStateOf(existente?.humedadActual?.toString() ?: "")
    }
    var error by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensaje()
        }
    }

    fun guardar() {
        val temp = temperatura.replace(',', '.').toDoubleOrNull()
        val hum = humedad.replace(',', '.').toDoubleOrNull()
        error = when {
            granja.isBlank() -> "La granja no puede estar vacía"
            nombre.isBlank() -> "El nombre no puede estar vacío"
            temp == null || temp < -20.0 || temp > 80.0 -> "La temperatura debe estar entre -20 y 80"
            hum == null || hum < 0.0 || hum > 100.0 -> "La humedad debe estar entre 0 y 100"
            else -> null
        }
        if (error != null || temp == null || hum == null) return

        if (esEdicion && existente != null) {
            viewModel.actualizarGalpon(
                existente.copy(
                    granja = granja.trim(),
                    nombre = nombre.trim(),
                    temperaturaActual = temp,
                    humedadActual = hum
                ),
                alTerminar = onVolver
            )
        } else {
            viewModel.crearGalpon(granja.trim(), nombre.trim(), temp, hum, alTerminar = onVolver)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (esEdicion) "Editar galpón" else "Nuevo galpón") },
                navigationIcon = { TextButton(onClick = onVolver) { Text("Cancelar") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = granja,
                onValueChange = { granja = it },
                label = { Text("Granja") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del galpón") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = temperatura,
                onValueChange = { temperatura = it },
                label = { Text("Temperatura (°C)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = humedad,
                onValueChange = { humedad = it },
                label = { Text("Humedad (%)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(20.dp))
            Button(onClick = { guardar() }, modifier = Modifier.fillMaxWidth()) {
                Text(if (esEdicion) "Guardar cambios" else "Crear galpón")
            }
        }
    }
}