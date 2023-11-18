package com.example.freedrawing.network

const val DEFAULT_SERVER_IP = "10.0.2.2"
const val DEFAULT_SERVER_PORT = 8080

sealed class MultiPlayerConfig

data class WebServerConfig(
    val roomID: String,
    val createRoom: Boolean,
    val userID: String,
    val serverIPv4: String = DEFAULT_SERVER_IP,
    val serverPort: Int = DEFAULT_SERVER_PORT,
) : MultiPlayerConfig()


data class BluetoothConfig(val TODO : Any)