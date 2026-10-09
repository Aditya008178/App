package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HealthInsight
import com.example.model.InsightType
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.RosePulse
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun MultiProgressRings(
    stepProgress: Float,
    calorieProgress: Float,
    waterProgress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 190.dp
) {
    val animSteps by animateFloatAsState(targetValue = stepProgress.coerceIn(0f, 1f), tween(800), label = "steps")
    val animCals by animateFloatAsState(targetValue = calorieProgress.coerceIn(0f, 1f), tween(900), label = "cals")
    val animWater by animateFloatAsState(targetValue = waterProgress.coerceIn(0f, 1f), tween(1000), label = "water")

    Box(
        modifier = modifier
            .size(size)
            .testTag("multi_progress_rings"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 13.dp.toPx()
            val spacing = 16.dp.toPx()

            // Outer ring: Steps (Emerald)
            val outerRadius = (this.size.minDimension - strokeWidth) / 2
            drawCircle(
                color = EmeraldPrimary.copy(alpha = 0.15f),
                radius = outerRadius,
                style = Stroke(strokeWidth)
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(EmeraldPrimary, Color(0xFF34D399), EmeraldPrimary)),
                startAngle = -90f,
                sweepAngle = animSteps * 360f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            // Middle ring: Calories (Orange Flame)
            val middleRadius = outerRadius - spacing
            drawCircle(
                color = OrangeFlame.copy(alpha = 0.15f),
                radius = middleRadius,
                style = Stroke(strokeWidth)
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(OrangeFlame, Color(0xFFFDBA74), OrangeFlame)),
                startAngle = -90f,
                sweepAngle = animCals * 360f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            // Inner ring: Water (Cyan)
            val innerRadius = middleRadius - spacing
            drawCircle(
                color = CyanSecondary.copy(alpha = 0.15f),
                radius = innerRadius,
                style = Stroke(strokeWidth)
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(CyanSecondary, Color(0xFF67E8F9), CyanSecondary)),
                startAngle = -90f,
                sweepAngle = animWater * 360f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(stepProgress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            )
            Text(
                text = "DAILY GOAL",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondaryDark,
                    letterSpacing = 1.2.sp
                )
            )
        }
    }
}

@Composable
fun MacroProgressBar(
    label: String,
    currentValue: Double,
    targetValue: Int,
    unit: String = "g",
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (targetValue > 0) (currentValue / targetValue).toFloat().coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, tween(600), label = label)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(barColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                )
            }
            Text(
                text = "${currentValue.toInt()} / ${targetValue}$unit",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondaryDark,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(barColor.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
fun HealthInsightCard(
    insight: HealthInsight,
    modifier: Modifier = Modifier
) {
    val (accentColor, icon) = when (insight.type) {
        InsightType.OPTIMAL -> Pair(EmeraldPrimary, Icons.Default.CheckCircle)
        InsightType.WARNING -> Pair(OrangeFlame, Icons.Default.Warning)
        InsightType.RECOMMENDATION -> Pair(CyanSecondary, Icons.Default.Info)
        InsightType.RECOVERY -> Pair(RosePulse, Icons.Default.Info)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("insight_card_${insight.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkNavyElevated),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = insight.tag.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = insight.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = insight.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondaryDark,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💡 Tip: ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = insight.actionSuggestion,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimaryDark.copy(alpha = 0.9f)
                        )
                    )
                }
            }
        }
    }
}
