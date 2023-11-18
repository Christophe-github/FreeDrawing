package com.example.freedrawing.mock

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.freedrawing.drawing.figure.Figure
import com.example.freedrawing.drawing.figure.Rectangle
import com.example.freedrawing.network.entities.PlayerInstructions
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class FakePlayer(private val userId: String) {

    val socket: FakeSocket = FakeSocket()

    private fun emitData(
        userId: String,
        newFigure: Figure? = null,
        updateFigure: Offset? = null,
        removeLastFigure: Boolean? = null,
        removeAllFigures: Boolean? = null
    ) {
        val playerInstructions = PlayerInstructions(
            userId,
            newFigure = newFigure,
            updateFigure = updateFigure,
            removeLastFigure = removeLastFigure,
            removeAllFigures = removeAllFigures
        )
        socket.write(Json.encodeToString(playerInstructions))
    }

    private suspend fun emitRectangleAndUpdates() {

        val startOffset = Offset(60f, 200f)
        val fig = Rectangle(
            startOffset,
            startOffset,
            Color.hsv(340f, 0.9f, 0.9f),
            Stroke(width = 40f, join = StrokeJoin.Round, cap = StrokeCap.Round)
        )

        emitData(userId, newFigure = fig)

        for (i in 1..300) {
            emitData(
                userId,
                updateFigure = startOffset + Offset(i * 1.5f, i * 1.5f)
            )
            delay(5)
        }
    }


    suspend fun startEmitting() = emitRectangleAndUpdates()




}