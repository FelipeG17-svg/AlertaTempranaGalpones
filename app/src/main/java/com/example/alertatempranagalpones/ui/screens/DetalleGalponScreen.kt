package com.example.alertatempranagalpones.ui.screens

//import androidx.compose.material3.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    viewModel: DetalleGalponViewModel = viewModel()
) {
    val galpon by viewModel.galpon.collectAsState()
    val mediciones by viewModel.mediciones.collectAsState()

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
                }
            )
        }
    ) { innerPadding ->
        val actual = galpon
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
                item {
                    Text("Historial de mediciones", style = MaterialTheme.typography.titleMedium)
                }
                if (mediciones.isEmpty()) {
                    item { Text("Aún no hay mediciones guardadas.") }
                } else {
                    items(mediciones, key = { it.id }) { medicion ->
                        FilaMedicion(medicion)
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenGalpon(galpon: Galpon) {
    val colorEstado = colorDeEstado(galpon.estado)
    val colorTexto = if (galpon.estado == EstadoGalpon.ADVERTENCIA) Color.Black else Color.White

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
                Surface(color = colorEstado, shape = RoundedCornerShape(50)) {
                    Text(
                        text = galpon.estado.etiqueta,
                        color = colorTexto,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
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


/* @Composable
fun DetalleGalponScreen(galponId: Long, onVolver: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Detalle del galpón $galponId", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = onVolver, modifier = Modifier.padding(top = 24.dp)) {
            Text("Volver")
        }
    }
} */