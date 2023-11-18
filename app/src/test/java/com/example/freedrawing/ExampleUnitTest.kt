package com.example.freedrawing

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import com.example.freedrawing.drawing.figure.Rectangle
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test


class ExampleUnitTest {


    @Test
    fun serializeTest() {

        val rect = Rectangle(Offset.Zero, Offset(2f, 3f), Color.Black, Fill)
        rect.update(Offset(5f, 5f))

        val rectStr = Json.encodeToString(rect)
        println(rectStr)

        val rect2 = Json.decodeFromString<Rectangle>(rectStr)
        println("${rect == rect2}")

        println(Json.encodeToString(rect2))
    }
}