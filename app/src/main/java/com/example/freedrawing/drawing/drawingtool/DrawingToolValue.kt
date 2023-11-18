package com.example.freedrawing.drawing.drawingtool

import androidx.annotation.DrawableRes
import com.example.freedrawing.R

//Enum used to expose drawing tools to the ui instead of the more
// complex DrawingTool
enum class DrawingToolValue {
    FreePathTool,
    RectangleTool,
    CircleTool,
    TriangleTool;

    fun toDrawingTool(userId: String) =
        when (this) {
            RectangleTool -> RectangleTool(userId)
            FreePathTool -> FreePathTool(userId)
            CircleTool -> CircleTool(userId)
            TriangleTool -> TriangleTool(userId)
        }


    @DrawableRes
    fun icon(): Int =
        when (this) {
            FreePathTool -> R.drawable.ic_line_24
            RectangleTool -> R.drawable.ic_square_24
            CircleTool -> R.drawable.ic_circle_24
            TriangleTool -> R.drawable.ic_triangle_24
        }


    fun description(): String =
        when (this) {
            FreePathTool -> "Free path tool"
            RectangleTool -> "Rectangle tool"
            CircleTool -> "Circle tool"
            TriangleTool -> "Triangle tool"
        }

}

