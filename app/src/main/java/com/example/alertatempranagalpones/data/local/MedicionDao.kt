package com.example.alertatempranagalpones.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.alertatempranagalpones.data.model.Medicion
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicionDao {

    @Insert
    suspend fun insertar(medicion: Medicion)

    @Insert
    suspend fun insertarTodas(mediciones: List<Medicion>)

    @Query("SELECT COUNT(*) FROM mediciones")
    suspend fun contar(): Int

    @Query("SELECT * FROM mediciones WHERE galponId = :galponId ORDER BY fechaHora DESC")
    fun observarPorGalpon(galponId: Long): Flow<List<Medicion>>
}


/* @Dao
interface MedicionDao {

    @Insert
    suspend fun insertar(medicion: Medicion)

    @Insert
    suspend fun insertarTodas(mediciones: List<Medicion>)

    @Query("SELECT * FROM mediciones WHERE galponId = :galponId ORDER BY fechaHora DESC")
    fun observarPorGalpon(galponId: Long): Flow<List<Medicion>>
} */