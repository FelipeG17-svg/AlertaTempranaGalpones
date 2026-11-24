package com.example.alertatempranagalpones.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.alertatempranagalpones.data.model.Galpon
import kotlinx.coroutines.flow.Flow

@Dao
interface GalponDao {

    @Query("SELECT * FROM galpones ORDER BY granja, nombre")
    fun observarTodos(): Flow<List<Galpon>>

    @Query("SELECT * FROM galpones WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Galpon?

    @Query("SELECT COUNT(*) FROM galpones")
    suspend fun contar(): Int

    @Upsert
    suspend fun guardarTodos(galpones: List<Galpon>)

    @Upsert
    suspend fun guardar(galpon: Galpon)

    @Query("DELETE FROM galpones WHERE id = :id")
    suspend fun borrarPorId(id: Long)

    @Query("DELETE FROM galpones WHERE id NOT IN (:ids)")
    suspend fun borrarQueNoEstenEn(ids: List<Long>)
}