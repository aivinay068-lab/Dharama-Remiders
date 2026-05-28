package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AiGuideScreen
import com.example.ui.screens.FestivalDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.TrackerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FestivalViewModel

class MainActivity : ComponentActivity() {

    // Permission request launcher for Android 13+ (API 33)
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Reminders enabled! You will meet sacred days ahead.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission denied. Please enable in settings to receive vrat reminders.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Ask for push permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: FestivalViewModel = viewModel()
                    MainAppLayout(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppLayout(viewModel: FestivalViewModel) {
    val festivals by viewModel.allFestivals.collectAsStateWithLifecycle()
    val selectedFestival by viewModel.selectedFestival.collectAsStateWithLifecycle()
    val aiGuidance by viewModel.aiGuidance.collectAsStateWithLifecycle()
    val isLoadingAi by viewModel.isLoadingAi.collectAsStateWithLifecycle()
    val aiChatLog by viewModel.aiChatLog.collectAsStateWithLifecycle()

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Utsavs, 1 = AI Guru, 2 = My Sadhana

    if (selectedFestival != null) {
        // Show detailed screen
        FestivalDetailScreen(
            festival = selectedFestival!!,
            aiGuidance = aiGuidance,
            isLoadingAi = isLoadingAi,
            onBackClick = { viewModel.selectFestival(null) },
            onToggleReminder = { viewModel.toggleReminderStatus(it) },
            onToggleCompleted = { id, isChecked -> viewModel.toggleCompleted(id, isChecked) },
            onSaveNote = { id, note -> viewModel.updateFestivalNote(id, note) },
            onAskGemini = { viewModel.askGeminiForFestivalGuidance(it) },
            onTriggerDemo = { viewModel.triggerDemoNotification(it) }
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("app_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        icon = { 
                            Icon(
                                imageVector = if (activeTab == 0) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                contentDescription = "Utsavs Tab"
                            ) 
                        },
                        label = { Text("Utsavs", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_tab_utsavs"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    )

                    NavigationBarItem(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        icon = { 
                            Icon(
                                imageVector = if (activeTab == 1) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                contentDescription = "AI Guru Tab"
                            ) 
                        },
                        label = { Text("AI Guru", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_tab_ai_guru"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    )

                    NavigationBarItem(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        icon = { 
                            Icon(
                                imageVector = if (activeTab == 2) Icons.Filled.Spa else Icons.Outlined.Spa,
                                contentDescription = "My Sadhana Tab"
                            ) 
                        },
                        label = { Text("My Sadhana", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_tab_sadhana"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "Tab Transition"
                ) { targetTab ->
                    when (targetTab) {
                        0 -> HomeScreen(
                            allFestivals = festivals,
                            onSelectFestival = { viewModel.selectFestival(it) },
                            onToggleReminder = { viewModel.toggleReminderStatus(it) }
                        )
                        1 -> AiGuideScreen(
                            chatLog = aiChatLog,
                            isLoading = isLoadingAi,
                            onSendQuestion = { viewModel.askGeminiCustomQuestion(it) },
                            onClearChat = { viewModel.clearChatLog() }
                        )
                        2 -> TrackerScreen(
                            allFestivals = festivals,
                            onAddCustomFestival = { name, category, date, tithi, deity, significance, whatToDo, isFasting ->
                                viewModel.insertCustomFestival(name, category, date, tithi, deity, significance, whatToDo, isFasting)
                            },
                            onDeleteFestival = { viewModel.deleteFestival(it) }
                        )
                    }
                }
            }
        }
    }
}
