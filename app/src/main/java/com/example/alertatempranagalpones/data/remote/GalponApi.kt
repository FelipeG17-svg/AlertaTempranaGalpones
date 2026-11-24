package com.example.alertatempranagalpones.data.remote

import com.example.alertatempranagalpones.data.model.Galpon
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GalponApi {

    @GET("api/galpones")
    suspend fun listar(): List<Galpon>

    @POST("api/galpones")
    suspend fun crear(@Body galpon: Galpon): Galpon

    @PUT("api/galpones/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body galpon: Galpon): Galpon

    @DELETE("api/galpones/{id}")
    suspend fun borrar(@Path("id") id: Long): Response<Unit>
}