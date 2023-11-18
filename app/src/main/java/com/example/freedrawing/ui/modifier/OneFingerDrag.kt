package com.example.freedrawing.ui.modifier

import android.view.MotionEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInteropFilter

private const val THRESHOLD_MOVE_UPDATE_SQUARED = 30f

/**
 * A modifier providing callbacks when dragging is detected with only one finger.
 *
 * Two or more fingers dragging is ignored.
 *
 * If two fingers touch the screen, dragging will only be re-enabled when all fingers have left
 * the screen
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.oneFingerDrag(
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: (Offset) -> Unit
): Modifier =
    composed {
        var twoPointers by remember { mutableStateOf(false) }
        var newPath by remember { mutableStateOf(true) }
        var currentPos by remember { mutableStateOf(Offset.Zero) }

        pointerInteropFilter { event ->
            val newPosition = Offset(event.x, event.y)

            if (event.pointerCount >= 2)
                twoPointers = true      //only reset when all fingers have left the screen

            if (event.action == MotionEvent.ACTION_DOWN) { //First finger to touch screen
                currentPos = newPosition
            } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) { // All fingers have stopped touching the screen
                // All fingers have left the screen or dragging was canceled
                if (!newPath) {
                    onDragEnd(newPosition)
                }

                twoPointers = false
                newPath = true
            }

            //Gesture with two pointers are used for panning and scaling,
            //so we disable drawing when two fingers are down to avoid
            //unwanted behavior
            if (!twoPointers &&
                event.action == MotionEvent.ACTION_MOVE &&
                newPosition.minus(currentPos).getDistanceSquared() > THRESHOLD_MOVE_UPDATE_SQUARED
            ) {
                if (newPath) {
                    newPath = false
                    onDragStart(newPosition)
                } else {
                    onDrag(newPosition)
                }
                currentPos = newPosition
            }
            true
        }

    }

