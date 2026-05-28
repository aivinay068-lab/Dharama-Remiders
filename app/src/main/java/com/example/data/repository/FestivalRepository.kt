package com.example.data.repository

import com.example.data.local.FestivalDao
import com.example.data.model.FestivalEntity
import kotlinx.coroutines.flow.Flow

class FestivalRepository(private val festivalDao: FestivalDao) {

    val allFestivals: Flow<List<FestivalEntity>> = festivalDao.getAllFestivals()

    suspend fun getAllFestivalsOnce(): List<FestivalEntity> {
        return festivalDao.getAllFestivalsOnce()
    }

    fun getFestivalById(id: Int): Flow<FestivalEntity?> {
        return festivalDao.getFestivalById(id)
    }

    suspend fun getFestivalByIdSync(id: Int): FestivalEntity? {
        return festivalDao.getFestivalByIdSync(id)
    }

    suspend fun insertFestival(festival: FestivalEntity): Long {
        return festivalDao.insertFestival(festival)
    }

    suspend fun insertFestivals(festivals: List<FestivalEntity>) {
        festivalDao.insertFestivals(festivals)
    }

    suspend fun updateFestival(festival: FestivalEntity) {
        festivalDao.updateFestival(festival)
    }

    suspend fun updateIsCompleted(id: Int, isCompleted: Boolean) {
        festivalDao.updateIsCompleted(id, isCompleted)
    }

    suspend fun updateFestivalNote(id: Int, userNote: String) {
        festivalDao.updateFestivalNote(id, userNote)
    }

    suspend fun updateReminderStatus(id: Int, isReminderEnabled: Boolean) {
        festivalDao.updateReminderStatus(id, isReminderEnabled)
    }

    suspend fun deleteFestival(festival: FestivalEntity) {
        festivalDao.deleteFestival(festival)
    }

    suspend fun deleteFestivalById(id: Int) {
        festivalDao.deleteFestivalById(id)
    }

    suspend fun getFestivalCount(): Int {
        return festivalDao.getFestivalCount()
    }
}
