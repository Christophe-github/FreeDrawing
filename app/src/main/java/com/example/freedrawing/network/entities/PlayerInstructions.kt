package com.example.freedrawing.network.entities

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.example.freedrawing.drawing.figure.Figure
import com.example.freedrawing.network.OffsetAsStringSerializer
import kotlinx.serialization.Serializable


@Serializable
@Immutable
class PlayerInstructions(
    val playerId: String,
    val newFigure: Figure? = null,

    @Serializable(with = OffsetAsStringSerializer::class)
    val updateFigure: Offset? = null,

    val removeLastFigure: Boolean? = null,
    val removeAllFigures: Boolean? = null,
)
