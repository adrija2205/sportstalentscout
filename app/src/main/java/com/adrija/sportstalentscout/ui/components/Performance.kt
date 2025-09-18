package com.adrija.sportstalentscout.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adrija.sportstalentscout.data.models.TestResult
import com.adrija.sportstalentscout.ui.theme.SportsTalentScoutTheme

@Composable
fun PerformanceChart(
    testResults: List<TestResult>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Performance Trend",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (testResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    val chartWidth = size.width
                    val chartHeight = size.height

                    if (testResults.isNotEmpty()) {
                        val maxScore = testResults.maxOf { it.score }
                        val minScore = testResults.minOf { it.score }
                        val scoreRange = maxScore - minScore

                        val path = Path()
                        var isFirst = true

                        testResults.forEachIndexed { index, result ->
                            val x = (index.toFloat() / (testResults.size - 1).coerceAtLeast(1)) * chartWidth
                            val y = if (scoreRange > 0) {
                                chartHeight - ((result.score - minScore).toFloat() / scoreRange) * chartHeight
                            } else {
                                chartHeight / 2
                            }

                            if (isFirst) {
                                path.moveTo(x, y)
                                isFirst = false
                            } else {
                                path.lineTo(x, y)
                            }

                            drawCircle(
                                color = Color.Blue,
                                radius = 6.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }

                        drawPath(
                            path = path,
                            color = Color.Blue,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (testResults.isNotEmpty()) {
                        Text(
                            text = "Min: ${testResults.minOf { it.score }}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Max: ${testResults.maxOf { it.score }}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PerformanceChartPreview() {
    SportsTalentScoutTheme {
        PerformanceChart(
            testResults = listOf(
                TestResult("Jump", "45", "cm", 75),
                TestResult("Jump", "48", "cm", 78),
                TestResult("Jump", "52", "cm", 85),
                TestResult("Jump", "49", "cm", 80),
                TestResult("Jump", "55", "cm", 90)
            )
        )
    }
}
