package com.example.freedrawing.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.freedrawing.drawing.PathStyle
import com.example.freedrawing.drawing.drawingtool.DrawingToolValue
import com.example.freedrawing.ui.theme.FreeDrawingTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun DrawingToolOptions(
    tool: DrawingToolValue,
    style: PathStyle,
    onStyleChanged: (PathStyle) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MaterialTheme.colors.surface.copy(alpha = 0.98f)),

        ) {

        PathStyleOptions(
            Modifier.fillMaxWidth(),
            pathStyle = style,
            onStyleChanged = onStyleChanged
        )

        Box(
            Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(bottomStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colors.primary.copy(alpha = 0.3f))
                .height(50.dp)
                .width(60.dp)
                .padding(12.dp)
        ) {
            Icon(
                painterResource(tool.icon()),
                tool.description(),
                Modifier.fillMaxSize(),
                tint = MaterialTheme.colors.primary
            )
        }
    }
}

@Preview
@Composable
fun DrawingToolOptionsPreview() {
    FreeDrawingTheme {
        DrawingToolOptions(DrawingToolValue.FreePathTool, PathStyle.default, onStyleChanged = {})
    }
}