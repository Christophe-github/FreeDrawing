package com.example.freedrawing.network.webserver

import android.util.Log
import com.example.freedrawing.network.DEFAULT_SERVER_IP
import com.example.freedrawing.network.DEFAULT_SERVER_PORT
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.create

object FreeDrawingRestAPIImpl {


    //With Retrofit changing the base URL is impossible without creating a new instance...
    fun get(serverIPv4: String, serverPort: Int): FreeDrawingRestAPI {
        return create(serverIPv4,serverPort)

    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun create(serverIPv4: String, serverPort: Int) : FreeDrawingRestAPI {
        val retrofit =
            Retrofit.Builder()
                .baseUrl("http://$serverIPv4:$serverPort")
                .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
                .build()

        return retrofit.create()
    }
}