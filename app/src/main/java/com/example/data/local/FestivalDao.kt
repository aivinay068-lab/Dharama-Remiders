package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FestivalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FestivalDao {
    @Query("SELECT * FROM festivals ORDER BY date ASC")
    fun getAllFestivals(): Flow<List<FestivalEntity>>

    @Query("SELECT * FROM festivals")
    suspend fun getAllFestivalsOnce(): List<FestivalEntity>

    @Query("SELECT * FROM festivals WHERE id = :id LIMIT 1")
    fun getFestivalById(id: Int): Flow<FestivalEntity?>

    @Query("SELECT * FROM festivals WHERE id = :id LIMIT 1")
    suspend fun getFestivalByIdSync(id: Int): FestivalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFestival(festival: FestivalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFestivals(festivals: List<FestivalEntity>)

    @Update
    suspend fun updateFestival(festival: FestivalEntity)

    @Query("UPDATE festivals SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateIsCompleted(id: Int, isCompleted: Boolean)

    @Query("UPDATE festivals SET userNote = :userNote WHERE id = :id")
    suspend fun updateFestivalNote(id: Int, userNote: String)

    @Query("UPDATE festivals SET isReminderEnabled = :isReminderEnabled WHERE id = :id")
    suspend fun updateReminderStatus(id: Int, isReminderEnabled: Boolean)

    @Delete
    suspend fun deleteFestival(festival: FestivalEntity)

    @Query("DELETE FROM festivals WHERE id = :id")
    suspend fun deleteFestivalById(id: Int)

    @Query("SELECT COUNT(*) FROM festivals")
    suspend fun getFestivalCount(): Int
}
