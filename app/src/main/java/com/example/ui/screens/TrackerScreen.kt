package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FestivalEntity
import java.time.LocalDate

@Composable
fun TrackerScreen(
    allFestivals: List<FestivalEntity>,
    onAddCustomFestival: (name: String, category: String, date: String, tithi: String, deity: String, significance: String, whatToDo: String, isFasting: Boolean) -> Unit,
    onDeleteFestival: (FestivalEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // Stats Calculations
    val totalVrats = remember(allFestivals) { allFestivals.filter { it.isFastingDay }.size }
    val vratsKept = remember(allFestivals) { allFestivals.filter { it.isFastingDay && it.isCompleted }.size }
    val totalRemindersActive = remember(allFestivals) { allFestivals.filter { it.isReminderEnabled }.size }
    
    val completionPercentage = remember(totalVrats, vratsKept) {
        if (totalVrats > 0) {
            (vratsKept.toFloat() / totalVrats.toFloat() * 100).toInt()
        } else {
            0
        }
    }

    // Custom days list
    val customDaysList = remember(allFestivals) { allFestivals.filter { it.isUserCreated } }

    // Form inputs state
    var isFormVisible by remember { mutableStateOf(false) }
    var inputName by remember { mutableStateOf("") }
    var inputDate by remember { mutableStateOf("2026-06-01") } // Seed format
    var inputTithi by remember { mutableStateOf("") }
    var inputDeity by remember { mutableStateOf("") }
    var inputSignificance by remember { mutableStateOf("") }
    var inputWhatToDo by remember { mutableStateOf("") }
    var inputIsFasting by remember { mutableStateOf(true) }

    var formErrorMessage by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Aesthetic Spiritual Header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "MY DAILY DEVOTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Sadhana Progress",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(text = "🕉️", fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Circular Progress Dashboard
        item {
            StatsSummaryCard(
                vratsKept = vratsKept,
                totalVrats = totalVrats,
                completionPercentage = completionPercentage,
                totalReminders = totalRemindersActive
            )
        }

        // Expandable Custom sacred day form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f)
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isFormVisible = !isFormVisible }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isFormVisible) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "Toggle add",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isFormVisible) "Collapse Form" else "Add Custom Sacred Day",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Add Day",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                AnimatedVisibility(
                    visible = isFormVisible,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Create Family Holy Days & Traditional Vrats",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = inputName,
                            onValueChange = { inputName = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_name_input"),
                            label = { Text("Sacred Day Name (e.g. Satyanarayan Pooja)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = inputDate,
                                onValueChange = { inputDate = it },
                                modifier = Modifier
                                    .weight(1.0f)
                                    .testTag("custom_date_input"),
                                label = { Text("Date (YYYY-MM-DD)") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                ),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = inputTithi,
                                onValueChange = { inputTithi = it },
                                modifier = Modifier
                                    .weight(1.0f)
                                    .testTag("custom_tithi_input"),
                                label = { Text("Tithi Details (Optional)") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                ),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = inputDeity,
                                onValueChange = { inputDeity = it },
                                modifier = Modifier
                                    .weight(1.0f)
                                    .testTag("custom_deity_input"),
                                label = { Text("Deity (Optional)") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                ),
                                singleLine = true
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1.0f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (inputIsFasting) Color(0xFFC8E6C9) else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { inputIsFasting = !inputIsFasting }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (inputIsFasting) "🥛 Fasting Day (Vrat)" else "🚩 Festive Day",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (inputIsFasting) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = inputSignificance,
                            onValueChange = { inputSignificance = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .testTag("custom_sig_input"),
                            label = { Text("Significance / Spiritual Story") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = inputWhatToDo,
                            onValueChange = { inputWhatToDo = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .testTag("custom_guideline_input"),
                            label = { Text("Devotional Guidelines (How to celebrate?)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            maxLines = 3
                        )

                        if (formErrorMessage.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formErrorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (inputName.trim().isEmpty() || inputDate.trim().isEmpty()) {
                                    formErrorMessage = "Name and Date fields are required."
                                } else if (!inputDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                                    formErrorMessage = "Date must be in YYYY-MM-DD standard."
                                } else {
                                    formErrorMessage = ""
                                    onAddCustomFestival(
                                        inputName,
                                        if (inputIsFasting) "Vrat" else "Festival",
                                        inputDate,
                                        if (inputTithi.isEmpty()) "Lunar Holy Tithi" else inputTithi,
                                        if (inputDeity.isEmpty()) "Lord Supreme" else inputDeity,
                                        if (inputSignificance.isEmpty()) "Beloved family traditional festival celebration." else inputSignificance,
                                        if (inputWhatToDo.isEmpty()) "Light a ghee lamp, perform clean pooja, and cook Satvik kheer." else inputWhatToDo,
                                        inputIsFasting
                                    )
                                    // Clear input variables
                                    inputName = ""
                                    inputTithi = ""
                                    inputDeity = ""
                                    inputSignificance = ""
                                    inputWhatToDo = ""
                                    isFormVisible = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("submit_custom_day"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Add to Sacred Calendar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Custom Days Section Title
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "My Custom Sacred Days",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
        }

        // Custom Days List / Empty State
        if (customDaysList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star empty",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No Custom Sacred Days Added",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Use the form above to log wedding anniversaries, family vrat katas, or traditional puja events.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        } else {
            items(customDaysList) { customFestival ->
                CustomDayListItem(
                    festival = customFestival,
                    onDelete = { onDeleteFestival(customFestival) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatsSummaryCard(
    vratsKept: Int,
    totalVrats: Int,
    completionPercentage: Int,
    totalReminders: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("stats_summary_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular progress gauge at the center
            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background circle track
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.2f),
                        startAngle = -220f,
                        sweepAngle = 260f,
                        useCenter = false,
                        style = Stroke(
                            width = 9.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }
                
                // Foreground progress circle
                val sweep = (completionPercentage.toFloat() / 100f) * 260f
                val primaryColor = MaterialTheme.colorScheme.primary
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = primaryColor,
                        startAngle = -220f,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(
                            width = 9.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }
                
                // Central text with flame emoji representation
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🪔",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$completionPercentage%",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "DEVOTIONAL PURITY",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Symmetric 2-column scorecard cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Vrats observed metric card
                Card(
                    modifier = Modifier.weight(1.0f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Vrats Logged",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$vratsKept of $totalVrats",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Fasts Observed",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Active reminders metric card
                Card(
                    modifier = Modifier.weight(1.0f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Active Reminder Alarms",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalReminders",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Reminders Active",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Helpful moral reminder block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🪔", fontSize = 16.sp)
                    Text(
                        text = "Consistent Sadhana cleanses the thoughts. Observance of holy vrat fasts establishes peace and higher consciousness in our home.",
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1.0f)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomDayListItem(
    festival: FestivalEntity,
    onDelete: () -> Unit
) {
    val (dayStr, monthStr) = try {
        val date = LocalDate.parse(festival.date)
        Pair(date.dayOfMonth.toString(), date.month.name.take(3))
    } catch (e: Exception) {
        Pair("--", "---")
    }

    val emoji = when {
        festival.deity.contains("Allah", ignoreCase = true) || festival.name.contains("Eid", ignoreCase = true) -> "🌙"
        festival.deity.contains("Jesus", ignoreCase = true) || festival.name.contains("Christmas", ignoreCase = true) || festival.name.contains("Friday", ignoreCase = true) -> "✝️"
        festival.deity.contains("Guru Nanak", ignoreCase = true) -> "☬"
        festival.deity.contains("Mahavira", ignoreCase = true) -> "🪷"
        festival.deity.contains("Buddha", ignoreCase = true) -> "☸️"
        festival.deity.contains("Vishnu", ignoreCase = true) || festival.deity.contains("Krishna", ignoreCase = true) -> "🥛"
        festival.deity.contains("Shiva", ignoreCase = true) -> "🔱"
        festival.category == "Vrat" -> "🥛"
        festival.category == "Festival" -> "🚩"
        else -> "✨"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("custom_item_${festival.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Symmetric Left Date Bubble matching HomeScreen
            Box(
                modifier = Modifier
                    .size(width = 50.dp, height = 50.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = dayStr,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = monthStr,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        lineHeight = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Deity icon circle indicator
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Symmetric Information Container
            Column(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = festival.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = festival.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "•",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Text(
                        text = festival.deity,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Compact red delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("delete_custom_button_${festival.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete custom item",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
