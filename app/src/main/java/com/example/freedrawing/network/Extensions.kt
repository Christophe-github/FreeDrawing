package com.example.freedrawing.network

import retrofit2.HttpException
import java.io.InputStreamReader

fun HttpException.errorBody(): String? =
    response()?.errorBody()?.byteStream()?.let {
        val i = InputStreamReader(it)
        val body = i.readText()
        i.close()
        body
    }