package com.example.alertatempranagalpones.data.model


data class Alerta(
    val id: Long = 0,
    val galponId: Long,
    val estado: EstadoGalpon,
    val mensaje: String,
    val fechaHora: Long,
    val atendida: Boolean = false,
    val accionRealizada: String? = null
)