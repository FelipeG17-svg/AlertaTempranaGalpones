package com.example.alertatempranagalpones.data.model


enum class EstadoGalpon(val etiqueta: String) {
    NORMAL("Normal"),
    ADVERTENCIA("Advertencia"),
    CRITICO("Crítico");

    companion object {
        const val UMBRAL_ADVERTENCIA = 28.0 // °C
        const val UMBRAL_CRITICO = 32.0     // °C

        fun desdeTemperatura(temperatura: Double): EstadoGalpon = when {
            temperatura >= UMBRAL_CRITICO -> CRITICO
            temperatura >= UMBRAL_ADVERTENCIA -> ADVERTENCIA
            else -> NORMAL
        }
    }
}