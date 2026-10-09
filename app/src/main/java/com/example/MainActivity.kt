package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entities.AchievementEntity
import com.example.service.NotificationHelper
import com.example.ui.dialogs.AddMealDialog
import com.example.ui.dialogs.AddReminderDialog
import com.example.ui.dialogs.AddWorkoutDialog
import com.example.ui.dialogs.EditProfileDialog
import com.example.ui.dialogs.ShareAchievementDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MealsScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.SocialScreen
import com.example.ui.screens.WearableScreen
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FitPulseTheme
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.FitPulseViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FitPulseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.initNotificationChannels(this)

        setContent {
            FitPulseTheme(darkTheme = true) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: FitPulseViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val shareAchievement by viewModel.selectedShareAchievement.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog state controllers
    var showAddMealDialog by remember { mutableStateOf(false) }
    var addMealTargetType by remember { mutableStateOf("Lunch") }
    var showAddWorkoutDialog by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    // Runtime Permission for Notifications (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Handle system back navigation to Home if on secondary tab
    BackHandler(enabled = currentTab != AppNavTab.HOME) {
        viewModel.selectTab(AppNavTab.HOME)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "FitPulse",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = EmeraldPrimary,
                            letterSpacing = 0.8.sp
                        )
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier.testTag("open_profile_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile & Goals",
                            tint = TextPrimaryDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkNavySurface
                ),
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkNavySurface,
                contentColor = EmeraldPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                val navItems = listOf(
                    Triple(AppNavTab.HOME, "Home", Icons.Default.Home),
                    Triple(AppNavTab.MEALS, "Meals", Icons.Default.Restaurant),
                    Triple(AppNavTab.DASHBOARD, "Trends", Icons.Default.Insights),
                    Triple(AppNavTab.WEARABLE, "Wearable", Icons.Default.Watch),
                    Triple(AppNavTab.REMINDERS, "Alerts", Icons.Default.Alarm),
                    Triple(AppNavTab.SOCIAL, "Social", Icons.Default.Group)
                )

                navItems.forEach { (tab, label, icon) ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkNavySurface,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = EmeraldPrimary,
                            unselectedIconColor = TextSecondaryDark,
                            unselectedTextColor = TextSecondaryDark
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentTab) {
                AppNavTab.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onOpenAddMeal = {
                            addMealTargetType = "Lunch"
                            showAddMealDialog = true
                        },
                        onOpenAddWorkout = { showAddWorkoutDialog = true }
                    )
                }
                AppNavTab.MEALS -> {
                    MealsScreen(
                        viewModel = viewModel,
                        onOpenAddMealForType = { type ->
                            addMealTargetType = type
                            showAddMealDialog = true
                        }
                    )
                }
                AppNavTab.DASHBOARD -> {
                    DashboardScreen(viewModel = viewModel)
                }
                AppNavTab.WEARABLE -> {
                    WearableScreen(viewModel = viewModel)
                }
                AppNavTab.REMINDERS -> {
                    RemindersScreen(
                        viewModel = viewModel,
                        onOpenAddReminder = { showAddReminderDialog = true }
                    )
                }
                AppNavTab.SOCIAL -> {
                    SocialScreen(
                        viewModel = viewModel,
                        onOpenShareDialog = { achievement ->
                            viewModel.setShareAchievement(achievement)
                        }
                    )
                }
            }
        }
    }

    // --- Overlays & Dialogs ---
    if (showAddMealDialog) {
        AddMealDialog(
            initialMealType = addMealTargetType,
            onDismiss = { showAddMealDialog = false },
            onSaveMeal = { type, food, portion, cals, protein, carbs, fat, fiber ->
                viewModel.addMeal(type, food, portion, cals, protein, carbs, fat, fiber)
            }
        )
    }

    if (showAddWorkoutDialog) {
        AddWorkoutDialog(
            onDismiss = { showAddWorkoutDialog = false },
            onSaveWorkout = { type, dur, cals, hr, dist, intensity ->
                viewModel.addWorkout(type, dur, cals, hr, dist, intensity)
            }
        )
    }

    if (showAddReminderDialog) {
        AddReminderDialog(
            onDismiss = { showAddReminderDialog = false },
            onSaveReminder = { title, cat, h, m, days, msg ->
                viewModel.addCustomReminder(title, cat, h, m, days, msg)
            }
        )
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentProfile = userProfile,
            onDismiss = { showEditProfileDialog = false },
            onSaveProfile = { w, h, level, goal, steps, water ->
                viewModel.updateProfile(w, h, level, goal, steps, water)
            }
        )
    }

    shareAchievement?.let { achievement ->
        ShareAchievementDialog(
            achievement = achievement,
            onDismiss = { viewModel.setShareAchievement(null) }
        )
    }
}
