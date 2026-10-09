package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entities.AchievementEntity
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
fun SocialScreen(
    viewModel: FitPulseViewModel,
    onOpenShareDialog: (AchievementEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val friendsFeed by viewModel.friendsFeed.collectAsStateWithLifecycle()
    var selectedSocialTab by remember { mutableIntStateOf(0) } // 0: Achievements & Badges, 1: Friends Feed

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("social_screen"),
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
                    text = "Social & Achievements",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryDark
                    )
                )
                Text(
                    text = "Share badges with friends, send high-fives and rank on leaderboards",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                )

                Spacer(modifier = Modifier.height(14.dp))

                TabRow(
                    selectedTabIndex = selectedSocialTab,
                    containerColor = DarkNavyElevated,
                    contentColor = EmeraldPrimary,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedSocialTab == 0,
                        onClick = { selectedSocialTab = 0 },
                        text = { Text("Achievements (${achievements.count { it.isUnlocked }})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedSocialTab == 1,
                        onClick = { selectedSocialTab = 1 },
                        text = { Text("Friends & Feed", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }

        if (selectedSocialTab == 0) {
            // Unlocked Badges Summary
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TROPHY ROOM",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, letterSpacing = 1.sp)
                            )
                            Text(
                                text = "${achievements.count { it.isUnlocked }} of ${achievements.size} Unlocked",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            )
                        }

                        Surface(
                            color = OrangeFlame.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "🔥 +475 XP Total",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OrangeFlame,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // Achievements List
            items(achievements) { achievement ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .testTag("achievement_card_${achievement.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (achievement.isUnlocked) DarkNavyElevated else DarkNavyElevated.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (achievement.isUnlocked) EmeraldPrimary.copy(alpha = 0.2f)
                                            else Color.Gray.copy(alpha = 0.2f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (achievement.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (achievement.isUnlocked) EmeraldPrimary else Color.Gray,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = achievement.title,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimaryDark
                                        )
                                    )
                                    Text(
                                        text = "${achievement.category} • +${achievement.xpReward} XP",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (achievement.isUnlocked) OrangeFlame else TextSecondaryDark,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            if (achievement.isUnlocked) {
                                Button(
                                    onClick = { onOpenShareDialog(achievement) },
                                    modifier = Modifier
                                        .height(36.dp)
                                        .testTag("share_btn_${achievement.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = DarkNavySurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Share", color = DarkNavySurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = achievement.description,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                        )

                        if (!achievement.isUnlocked) {
                            Spacer(modifier = Modifier.height(8.dp))
                            val prog = (achievement.currentProgress.toFloat() / achievement.targetValue.coerceAtLeast(1)).coerceIn(0f, 1f)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Progress: ${achievement.currentProgress} / ${achievement.targetValue}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                )
                                Text(
                                    text = "${(prog * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CyanSecondary, fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Friends Weekly Leaderboard Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Weekly Friends Leaderboard",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        listOf(
                            Triple("#1", "Elena Rostova", "12,450 steps • 🥇"),
                            Triple("#2", "Marcus Vance", "10,180 steps • 🥈"),
                            Triple("#3", "Alex Rivera (You)", "8,420 steps • 🥉"),
                            Triple("#4", "Chloe Chen", "8,940 steps")
                        ).forEach { (rank, name, stat) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = rank,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (rank == "#1") OrangeFlame else TextSecondaryDark
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (name.contains("You")) EmeraldPrimary else TextPrimaryDark
                                        )
                                    )
                                }
                                Text(
                                    text = stat,
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                )
                            }
                        }
                    }
                }
            }

            // Friends Activity Feed
            item {
                Text(
                    text = "Friend Activity Stream",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                )
            }

            items(friendsFeed) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .testTag("friend_card_${item.id}"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyElevated),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(item.avatarBgColorHex)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.initials,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimaryDark
                                        )
                                    )
                                    Text(
                                        text = item.timeAgo,
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                    )
                                }
                            }

                            if (item.achievementBadge != null) {
                                Surface(
                                    color = EmeraldPrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "🏆 ${item.achievementBadge}",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = EmeraldPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = item.activityText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimaryDark,
                                lineHeight = 20.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "👟 ${item.stepsToday} steps today",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                            )

                            // High Five Button
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.cheerFriend(item.id) }
                                    .testTag("high_five_${item.id}"),
                                color = if (item.isLikedByUser) EmeraldPrimary.copy(alpha = 0.25f) else DarkNavySurface
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = "Cheer",
                                        tint = if (item.isLikedByUser) EmeraldPrimary else TextSecondaryDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "High Five (${item.highFives})",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (item.isLikedByUser) EmeraldPrimary else TextSecondaryDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
