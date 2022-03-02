package org.smoke.sticky.tracker.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StickyDao {
    @Insert
    suspend fun insert(sticky: Sticky)

    @Query("SELECT * from sticky WHERE id = :id")
    fun getItem(id: Int): Flow<Sticky>

    @Query("SELECT * from sticky WHERE timeMillis BETWEEN :startTime AND :endTime ORDER BY timeMillis DESC")
    fun getRecentItems(startTime: Long, endTime: Long): Flow<List<Sticky>>

    @Query("SELECT SUM(amount) from sticky WHERE timeMillis BETWEEN :startTime AND :endTime")
    fun getRecentCount(startTime: Long, endTime: Long): Flow<Float?>
}