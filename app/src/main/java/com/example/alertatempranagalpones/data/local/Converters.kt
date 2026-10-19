package com.example.alertatempranagalpones.data.local

import androidx.room.TypeConverter
import com.example.alertatempranagalpones.data.model.EstadoGalpon

/**
 * Room solo sabe guardar números, textos y booleanos.
 * Estos conversores le enseñan a guardar el enum EstadoGalpon como texto.
 */
class Converters {

    @TypeConverter
    fun estadoATexto(estado: EstadoGalpon): String = estado.name

    @TypeConverter
    fun textoAEstado(texto: String): EstadoGalpon = EstadoGalpon.valueOf(texto)
}