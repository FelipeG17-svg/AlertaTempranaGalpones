package com.example.alertatempranagalpones.ui.screens

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import com.example.alertatempranagalpones.viewmodel.GalponesViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alertatempranagalpones.data.model.Alerta
import com.example.alertatempranagalpones.data.model.EstadoGalpon
import com.example.alertatempranagalpones.data.model.Galpon
import com.example.alertatempranagalpones.data.model.Medicion
import com.example.alertatempranagalpones.ui.theme.EstadoAdvertencia
import com.example.alertatempranagalpones.ui.theme.EstadoCritico
import com.example.alertatempranagalpones.ui.theme.EstadoNormal
import com.example.alertatempranagalpones.viewmodel.DetalleGalponViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

private fun colorDeEstado(estado: EstadoGalpon): Color = when (estado) {
    EstadoGalpon.NORMAL -> EstadoNormal
    EstadoGalpon.ADVERTENCIA -> EstadoAdvertencia
    EstadoGalpon.CRITICO -> EstadoCritico
}

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleGalponScreen(
    galponId: Long,
    onVolver: () -> Unit,
    onEditar: () -> Unit,
    viewModel: DetalleGalponViewModel = viewModel(),
    galponesViewModel: GalponesViewModel = viewModel()
) {
    val galpon by viewModel.galpon.collectAsState()
    val mediciones by viewModel.mediciones.collectAsState()
    val alertas by viewModel.alertas.collectAsState()

    var alertaEnRegistro by remember { mutableStateOf<Alerta?>(null) }

    val todos by galponesViewModel.galpones.collectAsState()
    val mensaje by galponesViewModel.mensaje.collectAsState()
    var confirmarBorrado by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            galponesViewModel.limpiarMensaje()
        }
    }

    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            title = { Text("Borrar galpón") },
            text = { Text("¿Seguro que quieres borrar ${galpon?.nombre ?: "este galpón"}? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarBorrado = false
                    galponesViewModel.borrarGalpon(galponId, onVolver)
                }) { Text("Borrar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBorrado = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(galpon?.nombre ?: "Detalle del galpón") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onEditar) { Text("Editar") }
                    TextButton(onClick = { confirmarBorrado = true }) { Text("Borrar") }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val actual = todos.firstOrNull { it.id == galponId } ?: galpon
        if (actual == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { ResumenGalpon(actual) }

                // --- Alertas ---
                item { Text("Alertas", style = MaterialTheme.typography.titleMedium) }
                if (alertas.isEmpty()) {
                    item { Text("Este galpón no tiene alertas.") }
                } else {
                    items(alertas, key = { "alerta-${it.id}" }) { alerta ->
                        TarjetaAlerta(
                            alerta = alerta,
                            onRegistrar = { alertaEnRegistro = alerta }
                        )
                    }
                }

                // --- Historial de mediciones ---
                item {
                    Text("Historial de mediciones", style = MaterialTheme.typography.titleMedium)
                }
                if (mediciones.isEmpty()) {
                    item { Text("Aún no hay mediciones guardadas.") }
                } else {
                    items(mediciones, key = { "medicion-${it.id}" }) { medicion ->
                        FilaMedicion(medicion)
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    // Formulario para registrar la acción realizada
    alertaEnRegistro?.let { alerta ->
        DialogoRegistrarAccion(
            alerta = alerta,
            onCancelar = { alertaEnRegistro = null },
            onGuardar = { texto ->
                val error = viewModel.registrarAccion(alerta.id, texto)
                if (error == null) alertaEnRegistro = null
                error
            }
        )
    }
}

@Composable
private fun EtiquetaEstado(estado: EstadoGalpon) {
    val colorTexto = if (estado == EstadoGalpon.ADVERTENCIA) Color.Black else Color.White
    Surface(color = colorDeEstado(estado), shape = RoundedCornerShape(50)) {
        Text(
            text = estado.etiqueta,
            color = colorTexto,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun ResumenGalpon(galpon: Galpon) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    galpon.granja,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                EtiquetaEstado(galpon.estado)
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ValorGrande(titulo = "Temperatura", valor = "%.1f °C".format(galpon.temperaturaActual))
                ValorGrande(titulo = "Humedad", valor = "%.1f %%".format(galpon.humedadActual))
            }
            Text(
                text = "Última actualización: " + formatoFecha.format(Date(galpon.ultimaActualizacion)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun ValorGrande(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            titulo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(valor, style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
private fun TarjetaAlerta(alerta: Alerta, onRegistrar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EtiquetaEstado(alerta.estado)
                Text(
                    text = if (alerta.atendida) "Atendida" else "Pendiente",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (alerta.atendida) EstadoNormal else MaterialTheme.colorScheme.error
                )
            }
            Text(alerta.mensaje, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = formatoFecha.format(Date(alerta.fechaHora)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (alerta.atendida) {
                Text(
                    text = "Acción realizada: " + (alerta.accionRealizada ?: "-"),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Button(onClick = onRegistrar) { Text("Registrar acción") }
            }
        }
    }
}

@Composable
private fun DialogoRegistrarAccion(
    alerta: Alerta,
    onCancelar: () -> Unit,
    onGuardar: (String) -> String?
) {
    var texto by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Registrar acción") },
        text = {
            Column {
                Text(alerta.mensaje, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = texto,
                    onValueChange = {
                        texto = it
                        error = null
                    },
                    label = { Text("¿Qué acción realizaste?") },
                    isError = error != null,
                    supportingText = {
                        Text(error ?: "${texto.length}/${DetalleGalponViewModel.MAX_CARACTERES}")
                    },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { error = onGuardar(texto) }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

@Composable
private fun FilaMedicion(medicion: Medicion) {
    val estado = EstadoGalpon.desdeTemperatura(medicion.temperatura)

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(colorDeEstado(estado), CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = formatoFecha.format(Date(medicion.fechaHora)),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "%.1f °C".format(medicion.temperatura),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "%.1f %%".format(medicion.humedad),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
