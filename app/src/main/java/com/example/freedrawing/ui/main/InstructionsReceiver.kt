package com.example.freedrawing.ui.main

import androidx.compose.ui.geometry.Offset
import com.example.freedrawing.drawing.PathStyle
import com.example.freedrawing.drawing.drawingtool.DrawingToolValue
import com.example.freedrawing.drawing.figure.Figure

/**
 * An interface describing the drawing instructions a listener can receive.
 * The FreeDrawingViewModel is capable of receiving these instructions : from
 * a local [DrawingTool] but also from the the network via a [InstructionsSharerService]
 */
interface InstructionsReceiver {
    fun newFigure(userId: String, figure: Figure)
    fun updateFigure(userId: String, offset: Offset)
    fun removeLastFigure(userId: String)
    fun removeAllFigures(userId: String)
    fun changeCurrentTool(userId: String, tool: DrawingToolValue)
    fun changeCurrentToolStyle(userId: String, style: PathStyle)
}