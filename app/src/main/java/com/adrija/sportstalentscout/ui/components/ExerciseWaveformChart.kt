package com.adrija.sportstalentscout.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adrija.sportstalentscout.data.models.AngleSample

@Composable
fun ExerciseWaveformChart(samples: List<AngleSample>) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Exercise Motion",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {

                if (samples.isEmpty()) return@Canvas

                // ---------- SMOOTH THE DATA ----------
                val smoothedAngles = samples
                    .map { it.angle }
                    .windowed(size = 5, step = 1, partialWindows = true) { window ->
                        window.average()
                    }

                val maxAngle = smoothedAngles.maxOrNull() ?: 0.0
                val minAngle = smoothedAngles.minOrNull() ?: 0.0

                val range = (maxAngle - minAngle).coerceAtLeast(1.0)

                val width = size.width
                val height = size.height

                val path = Path()

                smoothedAngles.forEachIndexed { i, angle ->

                    val x = (i.toFloat() / (smoothedAngles.size - 1).coerceAtLeast(1)) * width

                    val y = height - (
                        ((angle - minAngle) / range).toFloat() * height
                    )

                    if (i == 0) path.moveTo(x, y)
                    else path.lineTo(x, y)
                }

                drawPath(
                    path = path,
                    color = Color.Green,
                    style = Stroke(
                        width = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }
        }
    }
}