package com.example.freedrawing.drawing.drawingtool

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.freedrawing.drawing.PathStyle
import com.example.freedrawing.drawing.figure.Figure
import com.example.freedrawing.drawing.figure.Rectangle
import com.example.freedrawing.drawing.randomColor
import kotlin.random.Random


class RectangleTool(
    override val owner: String,
    override var style: PathStyle = PathStyle.default
) :
    DrawingTool() {


    override fun dragStart(offset: Offset) {

        val color = if (style.randomizeColor) randomColor() else style.color

        ir?.newFigure(owner, Rectangle(offset, offset, color, style.drawStyle))
    }

    override fun dragContinue(offset: Offset) {
        ir?.updateFigure(owner, offset)
    }

    override fun dragEnd(offset: Offset) = dragContinue(offset)

    override fun toDrawingToolValue(): DrawingToolValue = DrawingToolValue.RectangleTool


}
