package com.example.freedrawing.network.entities

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val message: String)