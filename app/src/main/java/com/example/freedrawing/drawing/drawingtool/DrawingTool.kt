package com.example.freedrawing.drawing.drawingtool

import androidx.compose.ui.geometry.Offset
import com.example.freedrawing.drawing.PathStyle
import com.example.freedrawing.ui.main.InstructionsReceiver


sealed class DrawingTool {

    abstract val owner: String
    abstract var style: PathStyle

    protected var ir : InstructionsReceiver? = null

    fun setInstructionsReceiver(receiver: InstructionsReceiver) { ir = receiver }
    fun removeInstructionsReceiver() { ir = null}

    abstract fun dragStart(offset: Offset)
    abstract fun dragContinue(offset: Offset)
    abstract fun dragEnd(offset: Offset)


    abstract fun toDrawingToolValue() : DrawingToolValue


}


