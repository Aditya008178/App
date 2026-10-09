package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.RosePulse
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.FitPulseViewModel

@Composable
fun DashboardScreen(
    viewModel: FitPulseViewModel,
    modifier: Modifier = Modifier
) {
    val recentMetrics by viewModel.recentMetrics.collectAsStateWithLifecycle()
    var selectedDaysIndex by remember { mutableIntStateOf(7) } // 7, 30, 90

    val filteredMetrics = remember(recentMetrics, selectedDaysIndex) {
        if (recentMetrics.isEmpty()) emptyList()
        else recentMetrics.take(selectedDaysIndex).reversed()
    }

    val avgSteps = if (filteredMetrics.isNotEmpty()) filteredMetrics.map { it.steps }.average().toInt() else 8500
    val avgActiveCals = if (filteredMetrics.isNotEmpty()) filteredMetrics.map { it.activeCalories }.average().toInt() else 490
    val avgSleep = if (filteredMetrics.isNotEmpty()) filteredMetrics.map { it.sleepHours }.average() else 7.4

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Long-Term Progress Trends",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryDark
                    )
                )
                Text(
                    text = "Historical trends, metabolic consistency & recovery",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Timeframe Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(7 to "7 Days", 30 to "30 Days", 90 to "90 Days").forEach { (days, label) ->
                        FilterChip(
                            selected = selectedDaysIndex == days,
                            onClick = { selectedDaysIndex = days },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = DarkNavySurface
                            )
                        )
                    }
                }
            }
        }

        // 2. High-Level Summary Stats
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Avg Steps
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyElevated),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DirectionsWalk,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Avg Steps", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$avgSteps",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Text("+12% vs last period", style = MaterialTheme.typography.labelSmall.copy(color = EmeraldPrimary, fontSize = 10.sp))
                    }
                }

                // Avg Burned
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyElevated),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = OrangeFlame,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Active Cals", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$avgActiveCals kcal",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Text("Consistent burn", style = MaterialTheme.typography.labelSmall.copy(color = OrangeFlame, fontSize = 10.sp))
                    }
                }

                // Avg Sleep
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyElevated),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CyanSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Avg Sleep", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${Math.round(avgSleep * 10.0) / 10.0} hrs",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Text("Optimal recovery", style = MaterialTheme.typography.labelSmall.copy(color = CyanSecondary, fontSize = 10.sp))
                    }
                }
            }
        }

        // 3. Step Count Trend Canvas Chart
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("steps_trend_card"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Daily Steps Trajectory",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            )
                        }
                        Text(
                            text = "Goal: 10,000",
                            style = MaterialTheme.typography.labelSmall.copy(color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Step Trend Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val maxStep = 13000f
                            val data = filteredMetrics.ifEmpty {
                                listOf(7800, 8400, 9200, 10500, 8900, 11200, 8420).map {
                                    com.example.data.entities.DailyMetricEntity(date = "", steps = it)
                                }
                            }
                            val stepCount = data.size
                            if (stepCount < 2) return@Canvas

                            val widthStep = size.width / (stepCount - 1)

                            // Baseline 10,000 goal line
                            val goalY = size.height - (10000f / maxStep * size.height)
                            drawLine(
                                color = EmeraldPrimary.copy(alpha = 0.35f),
                                start = Offset(0f, goalY),
                                end = Offset(size.width, goalY),
                                strokeWidth = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                            )

                            // Line path
                            val path = Path()
                            val fillPath = Path()
                            fillPath.moveTo(0f, size.height)

                            data.forEachIndexed { index, metric ->
                                val x = index * widthStep
                                val y = size.height - (metric.steps.toFloat() / maxStep * size.height).coerceIn(0f, size.height)
                                if (index == 0) {
                                    path.moveTo(x, y)
                                    fillPath.lineTo(x, y)
                                } else {
                                    path.lineTo(x, y)
                                    fillPath.lineTo(x, y)
                                }
                            }
                            fillPath.lineTo(size.width, size.height)
                            fillPath.close()

                            // Fill gradient
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    listOf(EmeraldPrimary.copy(alpha = 0.3f), Color.Transparent)
                                )
                            )

                            // Stroke line
                            drawPath(
                                path = path,
                                color = EmeraldPrimary,
                                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Draw data dots
                            data.forEachIndexed { index, metric ->
                                val x = index * widthStep
                                val y = size.height - (metric.steps.toFloat() / maxStep * size.height).coerceIn(0f, size.height)
                                drawCircle(
                                    color = EmeraldDark,
                                    radius = 5.dp.toPx(),
                                    center = Offset(x, y)
                                )
                                drawCircle(
                                    color = EmeraldPrimary,
                                    radius = 3.dp.toPx(),
                                    center = Offset(x, y)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Energy Expenditure vs Calorie Intake Comparison
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("calorie_balance_trend_card"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null,
                                tint = OrangeFlame,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Active Burn vs Intake",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(OrangeFlame))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Active Burn (kcal)", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyanSecondary))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Total Intake", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dual Bar Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val data = filteredMetrics.ifEmpty {
                                listOf(480, 520, 610, 430, 550, 640, 512).map {
                                    com.example.data.entities.DailyMetricEntity(date = "", activeCalories = it)
                                }
                            }
                            val count = data.size
                            val groupWidth = size.width / count
                            val barWidth = (groupWidth * 0.35f).coerceAtMost(16.dp.toPx())

                            data.forEachIndexed { index, metric ->
                                val groupX = index * groupWidth + (groupWidth / 2) - barWidth
                                val burnHeight = (metric.activeCalories.toFloat() / 800f * size.height).coerceIn(10f, size.height)
                                val intakeHeight = (1800f / 2500f * size.height * 0.8f).coerceIn(10f, size.height)

                                // Active burn bar (Orange)
                                drawRect(
                                    color = OrangeFlame,
                                    topLeft = Offset(groupX, size.height - burnHeight),
                                    size = Size(barWidth, burnHeight)
                                )

                                // Intake bar (Cyan)
                                drawRect(
                                    color = CyanSecondary.copy(alpha = 0.7f),
                                    topLeft = Offset(groupX + barWidth + 2.dp.toPx(), size.height - intakeHeight),
                                    size = Size(barWidth, intakeHeight)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Weight & Body Composition Curve
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("weight_progress_card"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weight Progression Trajectory",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Surface(
                            color = EmeraldPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "-1.0 kg (Target Pace)",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Current: 73.8 kg • Starting: 74.8 kg • Goal: 71.0 kg",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val path = Path()
                            val points = listOf(74.8f, 74.6f, 74.4f, 74.3f, 74.1f, 73.9f, 73.8f)
                            val minW = 73.5f
                            val maxW = 75.0f
                            val step = size.width / (points.size - 1)

                            points.forEachIndexed { i, w ->
                                val x = i * step
                                val y = size.height - ((w - minW) / (maxW - minW) * size.height)
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }

                            drawPath(
                                path = path,
                                color = CyanSecondary,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )

                            points.forEachIndexed { i, w ->
                                val x = i * step
                                val y = size.height - ((w - minW) / (maxW - minW) * size.height)
                                drawCircle(color = CyanSecondary, radius = 4.dp.toPx(), center = Offset(x, y))
                            }
                        }
                    }
                }
            }
        }
    }
}
