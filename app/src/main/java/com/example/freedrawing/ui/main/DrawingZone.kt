package com.example.freedrawing.ui.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.freedrawing.drawing.PathStyle
import com.example.freedrawing.drawing.drawingtool.DrawingToolValue
import com.example.freedrawing.ui.modifier.oneFingerDrag
import com.example.freedrawing.ui.theme.FreeDrawingTheme


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawingZone(
    state: List<UserState>,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    //The card is used to provide a shadow to the canvas
    Card(
        elevation = 10.dp,
        shape = RectangleShape,
        backgroundColor = MaterialTheme.colors.background,
        modifier = modifier
    ) {
        Canvas(
            Modifier
                .fillMaxSize()
                .clipToBounds() //to prevent drawing outside of the canvas
                .oneFingerDrag(
                    onDragStart = onDragStart,
                    onDrag = onDrag,
                    onDragEnd = onDragEnd
                )

        ) {
            state.forEach {
                it.history.forEach { fig ->
                    synchronized(this) {
                        fig.drawIn(this)
                    }
                }
            }
        }
    }

}

@Preview
@Composable
private fun DrawingZonePreview() {
    FreeDrawingTheme {
        DrawingZone(listOf(),{},{},{})
    }
}

