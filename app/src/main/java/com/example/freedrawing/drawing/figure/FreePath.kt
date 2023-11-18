package com.example.freedrawing.drawing.figure

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import com.example.freedrawing.network.ColorAsStringSerializer
import com.example.freedrawing.network.DrawStyleAsStringSerializer
import com.example.freedrawing.network.OffsetAsStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class FreePath(
    @Serializable(with = ColorAsStringSerializer::class) override val color: Color,
    @Serializable(with = DrawStyleAsStringSerializer::class) override val style: DrawStyle,
    private val pathNodes: MutableList<@Serializable(with = OffsetAsStringSerializer::class) Offset> = mutableListOf()
) :
    Figure() {

    @Transient
    private val path = Path()

    val nodes: List<Offset>
        get() = pathNodes

    init {
        if (pathNodes.isNotEmpty())
            path.moveTo(pathNodes[0].x, pathNodes[0].y)

        pathNodes.forEach {
            path.lineTo(it.x, it.y)
        }
    }

    override fun drawIn(scope: DrawScope) {
        scope.drawPath(path, color, style = style)
    }

    override fun update(offset: Offset) {
        if (pathNodes.isEmpty())
            path.moveTo(offset.x, offset.y)

        path.apply { lineTo(offset.x, offset.y) }
        pathNodes.add(offset)
    }


}




