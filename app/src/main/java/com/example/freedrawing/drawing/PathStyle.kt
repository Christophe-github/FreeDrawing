package com.example.freedrawing.drawing

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.freedrawing.network.ColorAsStringSerializer
import com.example.freedrawing.network.DrawStyleAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable
data class PathStyle(
    @Serializable(with = ColorAsStringSerializer::class) val color: Color,
    @Serializable(with = DrawStyleAsStringSerializer::class) val drawStyle: DrawStyle,
    val randomizeColor: Boolean = false
) {
    companion object {
        val default = PathStyle(Color.Black,Stroke.default())
    }
}



fun Stroke.Companion.default(width: Float = 30f) =
    Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)

