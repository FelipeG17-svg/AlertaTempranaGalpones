package com.example.alertatempranagalpones.data.model

data class Galpon(
    val id: Long = 0,
    val granja: String,
    val nombre: String,
    val temperaturaActual: Double,
    val humedadActual: Double,
    val ultimaActualizacion: Long, // milisegundos (System.currentTimeMillis())
    val estado: EstadoGalpon = EstadoGalpon.desdeTemperatura(temperaturaActual)
)