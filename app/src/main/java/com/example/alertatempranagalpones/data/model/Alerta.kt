package com.example.alertatempranagalpones.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Alerta generada cuando un galpón pasa a estado de advertencia o crítico.
 * Se guarda en la tabla "alertas".
 */
@Entity(
    tableName = "alertas",
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
data class Alerta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val galponId: Long,
    val estado: EstadoGalpon,
    val mensaje: String,
    val fechaHora: Long,
    val atendida: Boolean = false,
    val accionRealizada: String? = null
)


/* data class Alerta(
    val id: Long = 0,
    val galponId: Long,
    val estado: EstadoGalpon,
    val mensaje: String,
    val fechaHora: Long,
    val atendida: Boolean = false,
    val accionRealizada: String? = null
) */