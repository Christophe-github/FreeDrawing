package com.example.freedrawing.drawing.figure

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import kotlinx.serialization.Serializable

@Serializable
sealed class Figure {
    abstract val color: Color
    abstract val style: DrawStyle

    abstract fun drawIn(scope: DrawScope)
    abstract fun update(offset : Offset)
}

