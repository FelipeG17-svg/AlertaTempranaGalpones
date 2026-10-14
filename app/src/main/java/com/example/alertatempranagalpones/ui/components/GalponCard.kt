package com.example.alertatempranagalpones.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.alertatempranagalpones.data.model.EstadoGalpon
import com.example.alertatempranagalpones.data.model.Galpon
import com.example.alertatempranagalpones.ui.theme.EstadoAdvertencia
import com.example.alertatempranagalpones.ui.theme.EstadoCritico
import com.example.alertatempranagalpones.ui.theme.EstadoNormal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalponCard(
    galpon: Galpon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorEstado = when (galpon.estado) {
        EstadoGalpon.NORMAL -> EstadoNormal
        EstadoGalpon.ADVERTENCIA -> EstadoAdvertencia
        EstadoGalpon.CRITICO -> EstadoCritico
    }
    val colorTexto = if (galpon.estado == EstadoGalpon.ADVERTENCIA) Color.Black else Color.White

    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(galpon.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    galpon.granja,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Temperatura: %.1f °C".format(galpon.temperaturaActual), style = MaterialTheme.typography.bodyMedium)
                Text("Humedad: %.1f %%".format(galpon.humedadActual), style = MaterialTheme.typography.bodyMedium)
            }
            Surface(color = colorEstado, shape = RoundedCornerShape(50)) {
                Text(
                    text = galpon.estado.etiqueta,
                    color = colorTexto,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}