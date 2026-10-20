package com.example.alertatempranagalpones.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.alertatempranagalpones.data.model.Alerta
import com.example.alertatempranagalpones.data.model.Galpon
import com.example.alertatempranagalpones.data.model.Medicion
@Database(
    entities = [Galpon::class, Medicion::class, Alerta::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun galponDao(): GalponDao
    abstract fun medicionDao(): MedicionDao
    abstract fun alertaDao(): AlertaDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alerta_temprana.db"
                ).build().also { instancia = it }
            }
    }
}