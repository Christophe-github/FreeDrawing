package com.example.freedrawing.drawing.figure

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import com.example.freedrawing.network.ColorAsStringSerializer
import com.example.freedrawing.network.DrawStyleAsStringSerializer
import com.example.freedrawing.network.OffsetAsStringSerializer
import kotlinx.serialization.Serializable

@Serializable
class Circle(
    @Serializable(with = OffsetAsStringSerializer::class) val center: Offset,
    var radius: Float,
    @Serializable(with = ColorAsStringSerializer::class) override val color: Color,
    @Serializable(with = DrawStyleAsStringSerializer::class) override val style: DrawStyle
) : Figure() {


    override fun drawIn(scope: DrawScope) {
        scope.drawCircle(color,radius,center, style = style)
    }

    override fun update(offset: Offset) {
        radius = center.minus(offset).getDistance()
    }

}