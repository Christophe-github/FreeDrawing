package com.example.freedrawing.drawing

import android.util.Log
import androidx.compose.ui.graphics.Color
import kotlin.random.Random


fun randomColor() : Color {
    val range = 30
    val rand = Random.nextInt(range)


    return Color.hsv(360f * rand / range, 0.9f, 1f)
}