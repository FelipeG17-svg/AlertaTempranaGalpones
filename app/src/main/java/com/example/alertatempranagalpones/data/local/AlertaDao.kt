package com.example.alertatempranagalpones.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.alertatempranagalpones.data.model.Alerta
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertaDao {

    @Insert
    suspend fun insertar(alerta: Alerta): Long

    @Query("SELECT COUNT(*) FROM alertas")
    suspend fun contar(): Int

    @Query("SELECT * FROM alertas ORDER BY fechaHora DESC")
    fun observarTodas(): Flow<List<Alerta>>

    @Query("SELECT * FROM alertas WHERE galponId = :galponId ORDER BY fechaHora DESC")
    fun observarPorGalpon(galponId: Long): Flow<List<Alerta>>

    @Query("UPDATE alertas SET atendida = 1, accionRealizada = :accion WHERE id = :alertaId")
    suspend fun registrarAccion(alertaId: Long, accion: String)
}


/* @Dao
interface AlertaDao {

    @Insert
    suspend fun insertar(alerta: Alerta): Long

    @Query("SELECT * FROM alertas ORDER BY fechaHora DESC")
    fun observarTodas(): Flow<List<Alerta>>

    @Query("SELECT * FROM alertas WHERE galponId = :galponId ORDER BY fechaHora DESC")
    fun observarPorGalpon(galponId: Long): Flow<List<Alerta>>

    @Query("UPDATE alertas SET atendida = 1, accionRealizada = :accion WHERE id = :alertaId")
    suspend fun registrarAccion(alertaId: Long, accion: String)
} */