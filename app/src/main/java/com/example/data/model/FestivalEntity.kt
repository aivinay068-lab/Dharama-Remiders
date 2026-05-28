package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "festivals")
data class FestivalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // "Vrat", "Festival", "Special Day"
    val date: String, // "YYYY-MM-DD" e.g., "2026-06-25"
    val tithi: String, // e.g. "Ashadha Ekadashi"
    val deity: String, // e.g., "Lord Vishnu", "Lord Shiva", "None"
    val significance: String, // Meaning
    val whatToDo: String, // Ritual guidance (activities description)
    val mantras: String = "", // Chants
    val fastingRules: String = "", // Specific diets
    val isFastingDay: Boolean = false,
    val isReminderEnabled: Boolean = true,
    val isCompleted: Boolean = false, // True if user observed/completed rituals
    val userNote: String = "", // User dairy note/reflection
    val isUserCreated: Boolean = false // If true, it is added custom by the user
)
