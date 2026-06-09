package com.example.labball_tfg.Modelo

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singelton
object API {
    private const val BASE_URL = "http://10.0.2.2:8000/"
   //private const val BASE_URL = "http://192.168.1.165:8000/"

    // Se inicializa sólo cuando se necesit
    val apiDao: ModeloApiDao by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ModeloApiDao::class.java)
    }
}