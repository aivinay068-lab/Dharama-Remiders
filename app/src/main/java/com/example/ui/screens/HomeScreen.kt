package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FestivalEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// Core optimization helper functions
fun getDaysLeft(festivalDate: String, todayDate: String = "2026-05-27"): Long {
    return try {
        val fDate = LocalDate.parse(festivalDate)
        val tDate = LocalDate.parse(todayDate)
        ChronoUnit.DAYS.between(tDate, fDate)
    } catch (e: Exception) {
        999L
    }
}

fun formatDisplayDate(dateStr: String): String {
    return try {
        val date = LocalDate.parse(dateStr)
        val formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy")
        date.format(formatter)
    } catch (e: Exception) {
        dateStr
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    allFestivals: List<FestivalEntity>,
    onSelectFestival: (FestivalEntity) -> Unit,
    onToggleReminder: (FestivalEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") } // "All", "Vrat", "Festival", "Special Day"
    var showRitualDpDialog by remember { mutableStateOf(false) }

    // Today is set as 2026-05-27 according to current metadata
    val todayString = "2026-05-27"

    // Filter festivals
    val filteredFestivals = remember(allFestivals, searchQuery, selectedCategory) {
        allFestivals.filter { festival ->
            val matchesSearch = festival.name.contains(searchQuery, ignoreCase = true) ||
                    festival.deity.contains(searchQuery, ignoreCase = true) ||
                    festival.tithi.contains(searchQuery, ignoreCase = true)
            
            val matchesCategory = selectedCategory == "All" || festival.category == selectedCategory
            matchesSearch && matchesCategory
        }
    }

    // Split into upcoming and past
    val upcomingFestivals = remember(filteredFestivals, todayString) {
        filteredFestivals.filter { it.date >= todayString }
    }
    val pastFestivals = remember(filteredFestivals, todayString) {
        filteredFestivals.filter { it.date < todayString }
    }

    // Find nearest upcoming fast
    val nextFastingDay = remember(allFestivals, todayString) {
        allFestivals
            .filter { it.isFastingDay && it.date >= todayString }
            .minByOrNull { it.date }
    }

    // Ritual DP dialog on app face click integration
    if (showRitualDpDialog) {
        val todaysEvents = allFestivals.filter { it.date == todayString }
        AlertDialog(
            onDismissRequest = { showRitualDpDialog = false },
            confirmButton = {
                TextButton(onClick = { showRitualDpDialog = false }) {
                    Text("Deeksha Done", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.img_ritual_dp),
                        contentDescription = "Vedic Shubh Avatar",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Auspicious Daily Rituals", 
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Observe absolute high-integrity thoughts, practice meditation, and perform your ordained duties. This sacred display picture symbolizes the eternal inner flame of consciousness.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Today's Sacred Guidance:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (todaysEvents.isEmpty()) {
                        Text(
                            text = "• Today is a beautiful general day of self-reflection. Focus on simple eating and gratitude.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    } else {
                        todaysEvents.forEach { event ->
                            Text(
                                text = "✨ ${event.name} (${event.tithi}): Dedicated to ${event.deity}. ${event.significance}",
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 3.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(24.dp)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Traditional Vedic Sacred Header
            VedicHeader(todayString = todayString, onDpClick = { showRitualDpDialog = true })
        }

        nextFastingDay?.let { fast ->
            item {
                Spacer(modifier = Modifier.height(6.dp))
                UpcomingFastBanner(festival = fast, onSelect = { onSelectFestival(fast) })
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Search Bar with testTag
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input"),
                placeholder = { Text("Search fasts, deities, tithis...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Search")
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Category Filter Chips with testTags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("All", "Vrat", "Festival", "Special Day")
                categories.forEach { category ->
                    val categoryTag = "filter_${category.lowercase().replace(" ", "_")}"
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(if (category == "Vrat") "Vrats (Fasts)" else category) },
                        modifier = Modifier.testTag(categoryTag),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Horizontal Scroller block for Upcoming Days
        if (upcomingFestivals.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Upcoming Days Timeline",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(upcomingFestivals.take(8)) { festival ->
                            UpcomingDayScrollerCard(
                                festival = festival,
                                onCardClick = { onSelectFestival(festival) }
                            )
                        }
                    }
                }
            }
        }

        if (upcomingFestivals.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Fasting & Vrat Details",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
            items(upcomingFestivals) { festival ->
                FestivalListItemCard(
                    festival = festival,
                    onCardClick = { onSelectFestival(festival) },
                    onToggleReminder = { onToggleReminder(festival) }
                )
            }
        }

        if (pastFestivals.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Past Festivals / Observances",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
            items(pastFestivals) { festival ->
                FestivalListItemCard(
                    festival = festival,
                    onCardClick = { onSelectFestival(festival) },
                    onToggleReminder = { onToggleReminder(festival) }
                )
            }
        }

        if (filteredFestivals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Empty icon",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No festivals found match your criteria.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            "Try searching for Lord Vishnu, Shiva or Ekadashi.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VedicHeader(todayString: String, onDpClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.0f)) {
            Text(
                text = "VARANASI, UP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Dharma Reminders",
                fontSize = 24.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Display today's date traditionally
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = "Lotus icon",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Wednesday • Lunar Jyeshtha Shukla",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        // Display current date bubble and Ritual DP avatar on app face
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "27 MAY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "2026",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            // High priority Ritual DP on the face of the app
            Image(
                painter = painterResource(id = R.drawable.img_ritual_dp),
                contentDescription = "Ritual DP",
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .clickable { onDpClick() }
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(2.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun UpcomingDayScrollerCard(
    festival: FestivalEntity,
    onCardClick: () -> Unit
) {
    val days = getDaysLeft(festival.date)
    val countdownText = when {
        days == 0L -> "TODAY"
        days == 1L -> "TOMORROW"
        else -> "$days DAYS LEFT"
    }
    
    val badgeBgColor = when {
        days == 0L -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
        days == 1L -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    }
    val badgeTextColor = when {
        days == 0L -> MaterialTheme.colorScheme.secondary
        days == 1L -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primary
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
        festival.isUserCreated -> "✨"
        else -> "🗓️"
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(115.dp)
            .clickable { onCardClick() }
            .testTag("upcoming_scroller_card_${festival.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji / Icon representation with subtle background
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 14.sp)
                }

                // Countdown badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBgColor)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = countdownText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = badgeTextColor
                    )
                }
            }

            Column {
                Text(
                    text = festival.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = formatDisplayDate(festival.date),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun UpcomingFastBanner(festival: FestivalEntity, onSelect: () -> Unit) {
    val daysLeft = getDaysLeft(festival.date)
    val countdownText = when {
        daysLeft == 0L -> "Today!"
        daysLeft == 1L -> "Tomorrow!"
        else -> "In $daysLeft Days!"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("upcoming_fast_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Incorporate beautiful custom Ritual DP symbol here
                Image(
                    painter = painterResource(id = R.drawable.img_ritual_dp),
                    contentDescription = "Ritual DP Banner Logo",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1.0f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Next Sacred Fast",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = countdownText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = festival.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Date: ${formatDisplayDate(festival.date)} (${festival.tithi})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Guidance",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FestivalListItemCard(
    festival: FestivalEntity,
    onCardClick: () -> Unit,
    onToggleReminder: () -> Unit
) {
    val isPast = festival.date < "2026-05-27"

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
        festival.isUserCreated -> "✨"
        else -> "🗓️"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("festival_card_${festival.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isPast) {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPast) 0.dp else 2.dp
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left graphical block: Vertical formatted Date bubble
            Box(
                modifier = Modifier
                    .size(width = 50.dp, height = 50.dp)
                    .background(
                        color = if (isPast) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        },
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
                        color = if (isPast) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        lineHeight = 18.sp
                    )
                    Text(
                        text = monthStr,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPast) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        },
                        lineHeight = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Deity/Category symbol indicator
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = when (festival.category) {
                            "Vrat" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            "Festival" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f)
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Center details block
            Column(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = festival.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        color = if (isPast) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    
                    if (festival.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed Event",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

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
                        text = festival.tithi,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right interactive area (Countdown/Symmetric status badge + actions)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                if (!isPast) {
                    val days = getDaysLeft(festival.date)
                    val badgeText = when {
                        days == 0L -> "TODAY"
                        days == 1L -> "TOMORROW"
                        else -> "$days DAYS"
                    }
                    val badgeBg = when {
                        days == 0L -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        days == 1L -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    }
                    val badgeColor = when {
                        days == 0L -> MaterialTheme.colorScheme.secondary
                        days == 1L -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = badgeColor
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    IconButton(
                        onClick = onToggleReminder,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (festival.isReminderEnabled) {
                                Icons.Default.NotificationsActive
                            } else {
                                Icons.Default.NotificationsOff
                            },
                            contentDescription = "Toggle Reminders",
                            tint = if (festival.isReminderEnabled) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Text(
                        text = if (festival.isCompleted) "Completed" else "Passed",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (festival.isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}
