package com.example.alertatempranagalpones.data.model

/** Una medición de temperatura y humedad de un galpón en un momento dado. */
data class Medicion(
    val id: Long = 0,
    val galponId: Long,
    val temperatura: Double,
    val humedad: Double,
    val fechaHora: Long // milisegundos
)