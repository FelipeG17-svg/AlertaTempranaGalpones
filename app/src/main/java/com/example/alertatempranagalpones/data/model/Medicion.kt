package com.example.alertatempranagalpones.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Una medición de temperatura y humedad de un galpón en un momento dado.
 * Se guarda en la tabla "mediciones" (historial). Si se borra el galpón,
 * se borran también sus mediciones (CASCADE).
 */
@Entity(
    tableName = "mediciones",
    foreignKeys = [
        ForeignKey(
            entity = Galpon::class,
            parentColumns = ["id"],
            childColumns = ["galponId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("galponId")]
)
data class Medicion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val galponId: Long,
    val temperatura: Double,
    val humedad: Double,
    val fechaHora: Long // milisegundos
)



/*
/** Una medición de temperatura y humedad de un galpón en un momento dado. */
data class Medicion(
    val id: Long = 0,
    val galponId: Long,
    val temperatura: Double,
    val humedad: Double,
    val fechaHora: Long // milisegundos
 )*/