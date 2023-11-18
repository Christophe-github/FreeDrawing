package com.example.freedrawing.network.webserver

import com.example.freedrawing.network.entities.Room
import retrofit2.http.GET
import retrofit2.http.Path


interface FreeDrawingRestAPI {
    @GET("room/create/{roomID}")
    suspend fun createRoom(@Path("roomID") roomID: String): Room
}