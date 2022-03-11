package org.smoke.sticky.tracker.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StickyDao {
    @Insert
    suspend fun insert(sticky: Sticky)

    @Update
    suspend fun update(sticky: Sticky)

    @Delete
    suspend fun delete(sticky: Sticky)

    @Query("SELECT * from sticky WHERE id = :id")
    fun getItem(id: Int): Flow<Sticky>

    @Query("SELECT * from sticky WHERE timeMillis BETWEEN :startTime AND :endTime ORDER BY timeMillis DESC")
    fun getRecentItems(startTime: Long, endTime: Long): Flow<List<Sticky>>
}