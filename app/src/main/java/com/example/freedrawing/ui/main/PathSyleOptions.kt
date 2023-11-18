package com.example.freedrawing.ui.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.freedrawing.drawing.PathStyle
import com.example.freedrawing.drawing.default
import com.example.freedrawing.drawing.figure.Rectangle
import com.example.freedrawing.ui.common.ColorPickerDialog
import com.example.freedrawing.ui.theme.FreeDrawingTheme


@OptIn(ExperimentalGraphicsApi::class)
@Composable
fun PathStyleOptions(
    modifier: Modifier = Modifier,
    pathStyle: PathStyle = PathStyle.default,
    onStyleChanged: (PathStyle) -> Unit = {}
) {
    val stroke = pathStyle.drawStyle is Stroke
    val strokeWidth = (pathStyle.drawStyle as? Stroke)?.width
    var rememberedWidth by remember { mutableStateOf(30f) }
    var showColorPickerDialog by remember { mutableStateOf(false) }

    strokeWidth?.let {
        rememberedWidth = strokeWidth
    }


    if (showColorPickerDialog) {
        ColorPickerDialog(
            pathStyle.color,
            onColorSelected = { color ->
                onStyleChanged(pathStyle.copy(color = color))
            },
            onDismiss = { showColorPickerDialog = false }
        )
    }



    Column(Modifier.then(modifier).padding(vertical = 8.dp, horizontal = 16.dp)) {

        /////////////////////////////////////////
        // Top line with stroke color selector
        /////////////////////////////////////////


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { showColorPickerDialog = true }
                .padding(12.dp)
        ) {
            Text("Color")
            Spacer(Modifier.width(12.dp))
            Box(
                Modifier.size(24.dp)
                    .background(pathStyle.color, CircleShape)
                    .border(
                        2.dp, MaterialTheme.colors.primary, CircleShape
                    )
            )
        }

        /////////////////////////////////////////
        // Middle line with stroke / fill selector
        /////////////////////////////////////////

        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .toggleable(
                        value = stroke,
                        role = Role.RadioButton,
                        onValueChange = { onStyleChanged(pathStyle.copy(drawStyle = Stroke.default())) })
                    .padding(12.dp)
            ) {
                Text("Stroke")
                Spacer(Modifier.width(12.dp))
                RadioButton(stroke, onClick = null)
            }


            Row(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .toggleable(
                        value = !stroke,
                        role = Role.RadioButton,
                        onValueChange = { onStyleChanged(pathStyle.copy(drawStyle = Fill)) })
                    .padding(12.dp)
            ) {
                Text("Fill", Modifier.padding(end = 8.dp))
                RadioButton(!stroke, onClick = null)
            }


        }

        /////////////////////////////////////////
        // Bottom line with stroke width selector
        /////////////////////////////////////////


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {

            Text("Stroke Width")
            Spacer(Modifier.width(12.dp))
            Slider(
                rememberedWidth,
                onValueChange = {
                    onStyleChanged(pathStyle.copy(drawStyle = Stroke.default(width = it)))
                },
                valueRange = 10f..100f,
                enabled = stroke,
                modifier = Modifier.weight(1f)
            )
        }


        Row(
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .toggleable(
                    value = pathStyle.randomizeColor,
                    role = Role.Checkbox,
                    onValueChange = {
                        onStyleChanged(pathStyle.copy(randomizeColor = it))
                    })
                .padding(12.dp)
        ) {
            Text("Randomize color")
            Spacer(Modifier.width(12.dp))
            Checkbox(pathStyle.randomizeColor, onCheckedChange = null)
        }


    }
}


@Preview(showBackground = true)
@Composable
fun PathStyleOptionsPreview() {
    FreeDrawingTheme {
        PathStyleOptions()
    }
}


