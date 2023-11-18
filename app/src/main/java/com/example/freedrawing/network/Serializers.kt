package com.example.freedrawing.network

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


object ColorAsStringSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Color) {
        val string = value.value.toString()
//        val string = value.value.toString(16).padStart(6, '0')
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): Color {
        val string = decoder.decodeString()
        return Color(string.toULong())
//        return Color(string.toInt(16))
    }
}


object DrawStyleAsStringSerializer : KSerializer<DrawStyle> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("DrawStyle", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DrawStyle) {
        val string = when (value) {
            is Fill -> "Fill"
            else -> (value as Stroke).serialize()
        }
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): DrawStyle {
        val str = decoder.decodeString()
        if (str == "Fill") return Fill

        return deserializeStroke(str)
    }
}


private fun Stroke.serialize(): String {
    return "$width,$miter,$cap,$join"
}

private fun deserializeStroke(str: String): Stroke {
    val props = str.split(',')
    return Stroke(
        props[0].toFloat(),
        props[1].toFloat(),
        strokeCapFromString(props[2]),
        strokeJoinFromString(props[3])
    )
}

private fun strokeCapFromString(str: String): StrokeCap =
    when (str) {
        "Butt" -> StrokeCap.Butt
        "Round" -> StrokeCap.Round
        "Square" -> StrokeCap.Square
        else -> StrokeCap.Butt
    }

private fun strokeJoinFromString(str: String): StrokeJoin =
    when (str) {
        "Miter" -> StrokeJoin.Miter
        "Round" -> StrokeJoin.Round
        "Bevel" -> StrokeJoin.Bevel
        else -> StrokeJoin.Miter
    }


object OffsetAsStringSerializer : KSerializer<Offset> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Offset", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Offset) {
        val string = "${value.x},${value.y}"
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): Offset {
        val offset = decoder.decodeString().split(',')
        return Offset(offset[0].toFloat(), offset[1].toFloat())
    }
}

