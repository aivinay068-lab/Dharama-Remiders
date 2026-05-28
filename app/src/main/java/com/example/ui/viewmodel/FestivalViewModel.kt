package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.local.AppDatabase
import com.example.data.model.FestivalEntity
import com.example.data.repository.FestivalRepository
import com.example.notification.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FestivalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FestivalRepository
    
    // Live stream of all festivals
    val allFestivals: StateFlow<List<FestivalEntity>>

    // UI States
    private val _selectedFestival = MutableStateFlow<FestivalEntity?>(null)
    val selectedFestival: StateFlow<FestivalEntity?> = _selectedFestival.asStateFlow()

    private val _aiGuidance = MutableStateFlow<String>("")
    val aiGuidance: StateFlow<String> = _aiGuidance.asStateFlow()

    private val _isLoadingAi = MutableStateFlow(false)
    val isLoadingAi: StateFlow<Boolean> = _isLoadingAi.asStateFlow()

    // Chat history for AI general advice
    private val _aiChatLog = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList()) // Pair of (Text, isUser)
    val aiChatLog: StateFlow<List<Pair<String, Boolean>>> = _aiChatLog.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = FestivalRepository(database.festivalDao())
        
        allFestivals = repository.allFestivals
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Synchronize seeded festival list and reschedule active reminders on startup
        viewModelScope.launch {
            try {
                // Fetch current database records
                val existing = repository.getAllFestivalsOnce()
                val existingNames = existing.map { it.name }.toSet()
                
                // Identify and insert any missing seeds (e.g. newly added multi-religion days)
                val seeds = AppDatabase.getSeedFestivals()
                val missingSeeds = seeds.filter { it.name !in existingNames }
                if (missingSeeds.isNotEmpty()) {
                    repository.insertFestivals(missingSeeds)
                }
                
                // Reschedule active notifications for any upcoming occurrences
                val updatedList = repository.getAllFestivalsOnce()
                val todayStr = "2026-05-27"
                for (festival in updatedList) {
                    if (festival.isReminderEnabled && festival.date >= todayStr) {
                        NotificationHelper.scheduleNotification(application, festival)
                    }
                }
            } catch (e: Exception) {
                Log.e("FestivalViewModel", "Initialization and sync failed", e)
            }
        }
    }

    // Toggle festival completion (Vrat Kept)
    fun toggleCompleted(festivalId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateIsCompleted(festivalId, isCompleted)
            // Update selected festival references too
            val current = _selectedFestival.value
            if (current != null && current.id == festivalId) {
                _selectedFestival.value = current.copy(isCompleted = isCompleted)
            }
        }
    }

    // Edit private note for this spiritual day
    fun updateFestivalNote(festivalId: Int, note: String) {
        viewModelScope.launch {
            repository.updateFestivalNote(festivalId, note)
            val current = _selectedFestival.value
            if (current != null && current.id == festivalId) {
                _selectedFestival.value = current.copy(userNote = note)
            }
        }
    }

    // Toggle reminder alarm status
    fun toggleReminderStatus(festival: FestivalEntity) {
        val newStatus = !festival.isReminderEnabled
        viewModelScope.launch {
            repository.updateReminderStatus(festival.id, newStatus)
            val current = _selectedFestival.value
            if (current != null && current.id == festival.id) {
                _selectedFestival.value = current.copy(isReminderEnabled = newStatus)
            }

            if (newStatus) {
                // Schedule exact alarm via alarm manager
                NotificationHelper.scheduleNotification(getApplication(), festival.copy(isReminderEnabled = true))
            } else {
                // Cancel scheduled pending alarm
                NotificationHelper.cancelNotification(getApplication(), festival.id)
            }
        }
    }

    // Load details of selected festival
    fun selectFestival(festival: FestivalEntity?) {
        _selectedFestival.value = festival
        _aiGuidance.value = "" // Reset AI guidance when swapping festivals
    }

    // Quick notification test simulation
    fun triggerDemoNotification(festival: FestivalEntity) {
        NotificationHelper.triggerImmediateNotification(
            context = getApplication(),
            festivalName = festival.name,
            tithi = festival.tithi,
            deity = festival.deity,
            ritualsSummary = festival.significance,
            isFasting = festival.isFastingDay
        )
    }

    // Call Gemini to get deeper Pooja Vidhi, historical legends, mantras & satvik recipes
    fun askGeminiForFestivalGuidance(festival: FestivalEntity) {
        _isLoadingAi.value = true
        _aiGuidance.value = "Consulting the Vedic scriptures and rituals database..."
        
        val prompt = """
            You are an expert Hindu Vedic Scholar, Sanskrit Acharya, and Cultural Heritage Advisor. 
            Provide detailed, sacred guidance regarding the spiritual festival or fast: "${festival.name}".
            
            Details:
            - Category: ${festival.category}
            - Dedicated to: ${festival.deity}
            - Tithi: ${festival.tithi}
            - Core Meaning: ${festival.significance}
            
            Please generate a well-structured spiritual guide including:
            1. **Sacred Significance & Legend**: A brief 3-sentence summary of the story/spiritual importance.
            2. **Detailed Pooja Vidhi (Worship Ritual)**: A step-by-step list of what rituals should be performed on this day (morning bath, lighting lamp, offering water/flowers).
            3. **Auspicious Mantras to Chant**: Sincere chants or verses in transliteration or translation with their mental focus.
            4. **Satvik Diet Rules & Fasting Recipes**: If fasting is observed, what foods are recommended (such as specific fruits, buckwheat, potatoes, milk) and recipes allowed, and what is strictly avoided.
            5. **Essential Do's and Don'ts**: Direct, practical behavioral points for self-purification.
            
            Format with beautiful Markdown spacing, elegant bullet items, and clear bold headings. Keep the style highly respectful, traditional, yet understandable for modern busy individuals.
        """.trimIndent()

        viewModelScope.launch {
            val response = GeminiClient.generateSpiritualGuidance(prompt)
            _aiGuidance.value = response
            _isLoadingAi.value = false
        }
    }

    // Call Gemini for custom questions in the chat session
    fun askGeminiCustomQuestion(question: String) {
        if (question.trim().isEmpty()) return
        
        val currentChat = _aiChatLog.value.toMutableList()
        currentChat.add(Pair(question, true)) // Add user query
        _aiChatLog.value = currentChat
        
        _isLoadingAi.value = true
        
        val systemPrompt = """
            You are Sanskriti-AI, an expert Vedic Spiritual Advisor & Hindu Customs Guide.
            Answer the user's custom question with divine accuracy, high respect, and practical clarity.
            The user is asking about rituals, fasts, satvik recipes, auspicious days, or general spiritual guidance.
            Provide precise quotes (such as from Bhagavad Gita, Puranas, Upanishads) if relevant, translate, and explain practically.
            Keep your answers concise, scannable, and extremely welcoming. Focus strictly on spiritual and cultural matters.
        """.trimIndent()

        val fullQuery = "$systemPrompt\n\nUser Question: $question"

        viewModelScope.launch {
            val answer = GeminiClient.generateSpiritualGuidance(fullQuery)
            val updatedChat = _aiChatLog.value.toMutableList()
            updatedChat.add(Pair(answer, false)) // Add AI response
            _aiChatLog.value = updatedChat
            _isLoadingAi.value = false
        }
    }

    fun clearChatLog() {
        _aiChatLog.value = emptyList()
    }

    // Insert user custom religious day (e.g. family annual pooja, local village temple day, etc.)
    fun insertCustomFestival(
        name: String,
        category: String,
        date: String,
        tithi: String,
        deity: String,
        significance: String,
        whatToDo: String,
        isFastingDay: Boolean
    ) {
        viewModelScope.launch {
            val custom = FestivalEntity(
                name = name,
                category = category,
                date = date,
                tithi = tithi,
                deity = deity,
                significance = significance,
                whatToDo = whatToDo,
                isFastingDay = isFastingDay,
                isReminderEnabled = true,
                isUserCreated = true
            )
            val newId = repository.insertFestival(custom)
            Log.d("FestivalViewModel", "Added custom festival with id $newId")
            
            // Auto schedule notification for custom festival
            if (newId > 0) {
                NotificationHelper.scheduleNotification(getApplication(), custom.copy(id = newId.toInt()))
            }
        }
    }

    fun deleteFestival(festival: FestivalEntity) {
        viewModelScope.launch {
            repository.deleteFestival(festival)
            NotificationHelper.cancelNotification(getApplication(), festival.id)
            if (_selectedFestival.value?.id == festival.id) {
                _selectedFestival.value = null
            }
        }
    }
}
