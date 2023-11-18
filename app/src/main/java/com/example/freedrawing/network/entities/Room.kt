package com.example.freedrawing.network.entities

import kotlinx.serialization.Serializable

@Serializable
data class Room(val id: String, val passwordRequired: Boolean, val players: List<Player>)
