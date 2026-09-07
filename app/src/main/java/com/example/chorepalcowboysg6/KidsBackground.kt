package com.example.chorepalcowboysg6

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

@Composable
fun KidsBackground(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF73D3FF),
                        Color(0xFFC9F0FF),
                        Color(0xFFEAF9FF)
                    )
                )
            )
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            drawSun()

            drawCloud(
                centerX = size.width * 0.15f,
                centerY = size.height * 0.13f,
                scale = 1.1f
            )

            drawCloud(
                centerX = size.width * 0.82f,
                centerY = size.height * 0.25f,
                scale = 0.8f
            )

            drawCloud(
                centerX = size.width * 0.42f,
                centerY = size.height * 0.38f,
                scale = 0.55f
            )

            drawBackHills()
            drawFrontHills()
            drawFlowers()

            drawCleaningPug(
                x = size.width * 0.18f,
                y = size.height * 0.84f
            )
        }

        content()
    }
}

private fun DrawScope.drawSun() {

    val center = Offset(
        x = size.width * 0.85f,
        y = size.height * 0.09f
    )

    val radius = size.width * 0.075f

    drawCircle(
        color = Color(0xFFFFD54F),
        radius = radius,
        center = center
    )

    val rayColor = Color(0xFFFFD54F)

    drawLine(
        color = rayColor,
        start = Offset(center.x, center.y - radius * 1.5f),
        end = Offset(center.x, center.y - radius * 2f),
        strokeWidth = 10f
    )

    drawLine(
        color = rayColor,
        start = Offset(center.x, center.y + radius * 1.5f),
        end = Offset(center.x, center.y + radius * 2f),
        strokeWidth = 10f
    )

    drawLine(
        color = rayColor,
        start = Offset(center.x - radius * 1.5f, center.y),
        end = Offset(center.x - radius * 2f, center.y),
        strokeWidth = 10f
    )

    drawLine(
        color = rayColor,
        start = Offset(center.x + radius * 1.5f, center.y),
        end = Offset(center.x + radius * 2f, center.y),
        strokeWidth = 10f
    )
}

private fun DrawScope.drawCloud(
    centerX: Float,
    centerY: Float,
    scale: Float
) {

    val cloudColor = Color.White.copy(alpha = 0.88f)

    drawCircle(
        color = cloudColor,
        radius = 38f * scale,
        center = Offset(
            centerX - 38f * scale,
            centerY
        )
    )

    drawCircle(
        color = cloudColor,
        radius = 50f * scale,
        center = Offset(
            centerX,
            centerY - 12f * scale
        )
    )

    drawCircle(
        color = cloudColor,
        radius = 36f * scale,
        center = Offset(
            centerX + 43f * scale,
            centerY
        )
    )

    drawOval(
        color = cloudColor,
        topLeft = Offset(
            centerX - 80f * scale,
            centerY
        ),
        size = Size(
            160f * scale,
            48f * scale
        )
    )
}

private fun DrawScope.drawBackHills() {

    val path = Path().apply {

        moveTo(
            0f,
            size.height * 0.69f
        )

        quadraticBezierTo(
            size.width * 0.25f,
            size.height * 0.60f,
            size.width * 0.48f,
            size.height * 0.70f
        )

        quadraticBezierTo(
            size.width * 0.72f,
            size.height * 0.60f,
            size.width,
            size.height * 0.67f
        )

        lineTo(size.width, size.height)
        lineTo(0f, size.height)

        close()
    }

    drawPath(
        path = path,
        color = Color(0xFF77CFA1)
    )
}

private fun DrawScope.drawFrontHills() {

    val path = Path().apply {

        moveTo(
            0f,
            size.height * 0.77f
        )

        quadraticBezierTo(
            size.width * 0.30f,
            size.height * 0.67f,
            size.width * 0.58f,
            size.height * 0.80f
        )

        quadraticBezierTo(
            size.width * 0.78f,
            size.height * 0.70f,
            size.width,
            size.height * 0.75f
        )

        lineTo(size.width, size.height)
        lineTo(0f, size.height)

        close()
    }

    drawPath(
        path = path,
        color = Color(0xFF78D64B)
    )
}

private fun DrawScope.drawFlowers() {

    drawFlower(
        x = size.width * 0.56f,
        y = size.height * 0.82f
    )

    drawFlower(
        x = size.width * 0.91f,
        y = size.height * 0.88f
    )

    drawFlower(
        x = size.width * 0.67f,
        y = size.height * 0.74f
    )
}

private fun DrawScope.drawFlower(
    x: Float,
    y: Float
) {

    drawCircle(
        color = Color.White,
        radius = 11f,
        center = Offset(x - 10f, y)
    )

    drawCircle(
        color = Color.White,
        radius = 11f,
        center = Offset(x + 10f, y)
    )

    drawCircle(
        color = Color.White,
        radius = 11f,
        center = Offset(x, y - 10f)
    )

    drawCircle(
        color = Color.White,
        radius = 11f,
        center = Offset(x, y + 10f)
    )

    drawCircle(
        color = Color(0xFFFFC107),
        radius = 8f,
        center = Offset(x, y)
    )
}

private fun DrawScope.drawCleaningPug(
    x: Float,
    y: Float
) {

    val scale = size.width / 1080f

    drawOval(
        color = Color(0xFFEAB67A),
        topLeft = Offset(
            x - 85f * scale,
            y - 25f * scale
        ),
        size = Size(
            170f * scale,
            145f * scale
        )
    )

    drawCircle(
        color = Color(0xFFF2BF80),
        radius = 90f * scale,
        center = Offset(
            x,
            y - 85f * scale
        )
    )

    drawOval(
        color = Color(0xFF4A332B),
        topLeft = Offset(
            x - 105f * scale,
            y - 155f * scale
        ),
        size = Size(
            65f * scale,
            95f * scale
        )
    )

    drawOval(
        color = Color(0xFF4A332B),
        topLeft = Offset(
            x + 42f * scale,
            y - 155f * scale
        ),
        size = Size(
            65f * scale,
            95f * scale
        )
    )

    drawOval(
        color = Color(0xFF40302A),
        topLeft = Offset(
            x - 50f * scale,
            y - 90f * scale
        ),
        size = Size(
            100f * scale,
            70f * scale
        )
    )

    drawCircle(
        color = Color.Black,
        radius = 17f * scale,
        center = Offset(
            x - 34f * scale,
            y - 105f * scale
        )
    )

    drawCircle(
        color = Color.Black,
        radius = 17f * scale,
        center = Offset(
            x + 34f * scale,
            y - 105f * scale
        )
    )

    drawCircle(
        color = Color.White,
        radius = 5f * scale,
        center = Offset(
            x - 28f * scale,
            y - 111f * scale
        )
    )

    drawCircle(
        color = Color.White,
        radius = 5f * scale,
        center = Offset(
            x + 40f * scale,
            y - 111f * scale
        )
    )

    drawOval(
        color = Color.Black,
        topLeft = Offset(
            x - 16f * scale,
            y - 80f * scale
        ),
        size = Size(
            32f * scale,
            22f * scale
        )
    )

    drawOval(
        color = Color(0xFFFF7B88),
        topLeft = Offset(
            x - 16f * scale,
            y - 49f * scale
        ),
        size = Size(
            32f * scale,
            42f * scale
        )
    )

    val bandana = Path().apply {

        moveTo(
            x - 65f * scale,
            y - 10f * scale
        )

        lineTo(
            x + 65f * scale,
            y - 10f * scale
        )

        lineTo(
            x,
            y + 65f * scale
        )

        close()
    }

    drawPath(
        path = bandana,
        color = Color(0xFF129447)
    )

    drawOval(
        color = Color(0xFFF2BF80),
        topLeft = Offset(
            x - 82f * scale,
            y + 62f * scale
        ),
        size = Size(
            58f * scale,
            80f * scale
        )
    )

    drawOval(
        color = Color(0xFFF2BF80),
        topLeft = Offset(
            x + 22f * scale,
            y + 62f * scale
        ),
        size = Size(
            58f * scale,
            80f * scale
        )
    )

    drawOval(
        color = Color.White,
        topLeft = Offset(
            x + 38f * scale,
            y + 110f * scale
        ),
        size = Size(
            130f * scale,
            55f * scale
        )
    )

    drawLine(
        color = Color(0xFFD9E7EE),
        start = Offset(
            x + 65f * scale,
            y + 125f * scale
        ),
        end = Offset(
            x + 135f * scale,
            y + 140f * scale
        ),
        strokeWidth = 4f * scale
    )

    drawLine(
        color = Color(0xFF3D3D3D),
        start = Offset(
            x + 145f * scale,
            y + 90f * scale
        ),
        end = Offset(
            x + 165f * scale,
            y + 110f * scale
        ),
        strokeWidth = 5f * scale
    )

    drawLine(
        color = Color(0xFF3D3D3D),
        start = Offset(
            x + 155f * scale,
            y + 70f * scale
        ),
        end = Offset(
            x + 180f * scale,
            y + 80f * scale
        ),
        strokeWidth = 5f * scale
    )
}

@Composable
fun ChorePalScreenBackground(
    isKidsMode: Boolean,
    content: @Composable () -> Unit
) {

    if (isKidsMode) {

        KidsBackground {
            content()
        }

    } else {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            content()
        }
    }
}

