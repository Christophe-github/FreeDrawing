package com.example.freedrawing.network.entities

import kotlinx.serialization.Serializable

@Serializable
data class FreeDrawingSocketStatus(val accepted: Boolean, val error: String? = null)
