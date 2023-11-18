package com.example.freedrawing.drawing.figure


import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import com.example.freedrawing.network.ColorAsStringSerializer
import com.example.freedrawing.network.DrawStyleAsStringSerializer
import com.example.freedrawing.network.OffsetAsStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.sqrt


@Serializable
class Triangle(
    @Serializable(with = OffsetAsStringSerializer::class) val firstPoint: Offset,
    @Serializable(with = OffsetAsStringSerializer::class) var secondPoint: Offset,
    @Serializable(with = ColorAsStringSerializer::class) override val color: Color,
    @Serializable(with = DrawStyleAsStringSerializer::class) override val style: DrawStyle
) : Figure() {

    @Serializable(with = OffsetAsStringSerializer::class)
    private var thirdPoint: Offset? = null

    @Transient
    private var path = Path()

    init {
        computeThirdPoint()
    }

    override fun drawIn(scope: DrawScope) {
        scope.drawPath(path, color = color, style = style)
    }

    override fun update(offset: Offset) {
        secondPoint = offset
        computeThirdPoint()
    }

    private fun computeThirdPointA() {
//        val firstPoint = Offset(0f,0f)
//        val secondPoint = Offset(1f,1f)


        val (x1, y1) = secondPoint.minus(firstPoint)

        val x1Squared = x1 * x1
        val y1Squared = y1 * y1
        val s = x1Squared + y1Squared

        val a = (1 + x1Squared / y1Squared)
        val b = -2 * s * (x1 / 2 * y1Squared)
        val c = (s * s / (4 * y1Squared)) - s

        val x = (-b + sqrt(b * b - 4 * a * c)) / (2 * a)
        val y = (s - 2 * x1 * x) / (2 * y1)
//        val y = sqrt(s - x * x)

        thirdPoint = Offset(x, y).plus(firstPoint)
//        Log.d("###########", "point $x $y")

    }

    private fun computeThirdPoint() {
        if (firstPoint == secondPoint) {
            thirdPoint = firstPoint
            return
        }

        // We are creating an equilateral triangle
        // x1² + x2² is the distance (squared) between (0,0) and (x1,y1), noted d

        //Base equations (circle equations)
        // x² + y² = x1² + x2²               point must be at d distance from (0,0)
        // (x-x1)² + (y-y1)² = x1² + y1²     point must be at d distance from (x1,y1)

        //From this we obtain two expressions for y :
        // y = sqrt(x1² + x2² - x²)
        // y = (x1² + y1² - 2x1x) / (2y1)

        //By injecting the first one in the second equation, we obtain (after simplifying)
        // 4x² - 4x1x - 4y1² + x1² + y1² = 0

        //This is now a polynomial solving issue

        val (x1, y1) = secondPoint.minus(firstPoint)

        val a = 4
        val b = -4 * x1
        val c = -4 * y1 * y1 + x1 * x1 + y1 * y1

        val x = (-b + sqrt(b * b - 4 * a * c)) / (2 * a)
//        val y = sqrt(x1 * x1 + y1 * y1 - x * x)

        //The second expression for y is better because it gives the right sign
        //But when y1 is 0 we can't use it so we use the first one (where y is always positive)
        val y = if (y1 == 0f)
            sqrt(x1 * x1 + y1 * y1 - x * x)
        else
            (x1 * x1 + y1 * y1 - 2 * x1 * x) / (2 * y1)



        if (!x.isNaN() && !y.isNaN())
            thirdPoint = Offset(x, y).plus(firstPoint)

//        Log.d("###########", "(0,0) ($x1,$y1) -> ($x,$y)")
//        Log.d(
//            "###########",
//            "(${firstPoint.x},${firstPoint.y}) (${secondPoint.x},${secondPoint.y}) -> (${thirdPoint?.x},${thirdPoint?.y})"
//        )

        thirdPoint?.let {
            path = Path().apply {
                moveTo(firstPoint.x, firstPoint.y)
                lineTo(secondPoint.x, secondPoint.y)
                lineTo(it.x, it.y)
                close()
            }
        }
    }

}

