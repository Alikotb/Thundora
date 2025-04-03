package com.example.thundora.view.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thundora.R
import com.example.thundora.view.components.getBackgroundColor

@Composable
fun LineChart(
    dataPoints: List<Float>,
    descriptions: List<String>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.White,
    lineWidth: Float = 4f,
) {
    if (dataPoints.isEmpty()) return
    val maxValue = dataPoints.maxOrNull() ?: 1f
    Box(modifier = modifier) {
        Canvas(modifier = modifier) {
            val widthStep = size.width / (dataPoints.size - 1)
            val path = Path().apply {
                moveTo(0f, size.height - (dataPoints[0] / maxValue) * size.height)
                dataPoints.forEachIndexed { index, dataPoint ->
                    lineTo(
                        index * widthStep,
                        size.height - (dataPoint / maxValue) * size.height
                    )
                }
            }
            drawPath(path, color = lineColor, style = Stroke(width = lineWidth))
            dataPoints.forEachIndexed { index, dataPoint ->
                val x = index * widthStep
                val y = size.height - (dataPoint / maxValue) * size.height
                drawCircle(color = Color.Red, radius = 6f, center = Offset(x, y))
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                descriptions.forEach { description ->
                    Text(
                        text = "$description\n",
                        fontSize = 14.sp,
                        color = lineColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(64.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LineChartScreen(
    dataPoints: MutableList<Float>,
    data: MutableList<String>,
    isDayTime: Boolean,
    weatherCondition: String,
    color: Color,
) {
    val backgroundColor = getBackgroundColor(isDayTime, weatherCondition)
    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.temperature), fontSize = 20.sp, color = color)
            Spacer(modifier = Modifier.height(16.dp))
            LineChart(
                dataPoints = dataPoints,
                descriptions = data,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                lineColor = color,
            )
        }
    }
}