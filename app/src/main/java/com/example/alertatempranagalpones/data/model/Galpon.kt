package com.example.alertatempranagalpones.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Un galpón avícola de una granja. Se guarda en la tabla "galpones". */
@Entity(tableName = "galpones")
data class Galpon(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val granja: String,
    val nombre: String,
    val temperaturaActual: Double,
    val humedadActual: Double,
    val ultimaActualizacion: Long,
    val estado: EstadoGalpon = EstadoGalpon.desdeTemperatura(temperaturaActual)
)





/*data class Galpon(
    val id: Long = 0,
    val granja: String,
    val nombre: String,
    val temperaturaActual: Double,
    val humedadActual: Double,
    val ultimaActualizacion: Long, // milisegundos (System.currentTimeMillis())
    val estado: EstadoGalpon = EstadoGalpon.desdeTemperatura(temperaturaActual)
) */