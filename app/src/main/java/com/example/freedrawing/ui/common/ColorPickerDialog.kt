package com.example.freedrawing.ui.common

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.freedrawing.ui.theme.FreeDrawingTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun ColorPickerDialog(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val numberOfColors = 18
    val dismissCoroutine = rememberCoroutineScope()

    val colors = remember {
        MutableList(numberOfColors) { i ->
            Color.hsv(i * 360f / numberOfColors, 0.9f, 1f)
        }.apply {
            add(Color.White)
            add(Color.Black)
        }
    }



    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colors.background)
                .padding(top = 24.dp)
        ) {
            Text("Choose a color",
                Modifier.padding(horizontal = 24.dp),
                style = MaterialTheme.typography.h5)
            Spacer(Modifier.height(24.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 48.dp),
                Modifier.padding(horizontal = 24.dp)
            ) {



                items(colors) { color ->
                    Box(Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            dismissCoroutine.launch {
                                onColorSelected(color)
                                delay(300)
                                onDismiss()
                            }
                        }
                    ) {

                        androidx.compose.animation.AnimatedVisibility(
                            visible = color == selectedColor,
                            enter = scaleIn(
                                spring(
                                    dampingRatio = Spring.DampingRatioHighBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ), initialScale = 0.9f
                            ),
                            exit = scaleOut(targetScale = 0.8f),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Box(
                                Modifier
                                    .size(46.dp)
                                    .background(MaterialTheme.colors.primary, shape = CircleShape)
                            )
                        }

                        Box(
                            Modifier
                                .size(38.dp)
                                .background(color, shape = CircleShape)
                                .align(Alignment.Center)
                        )


                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(),
                modifier = Modifier.fillMaxWidth(0.5f).height(55.dp).align(Alignment.End)
            ) {
                Text("Dismiss")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ColorPickerDialogPreview() {
    FreeDrawingTheme {
        ColorPickerDialog(Color.Black, {}) {}
    }
}

@Preview(showBackground = true, widthDp = 250, fontScale = 1.3f)
@Composable
private fun ColorPickerDialogPreviewSmall() {
    FreeDrawingTheme {

        ColorPickerDialog(Color.Black, {}) {}
    }
}
