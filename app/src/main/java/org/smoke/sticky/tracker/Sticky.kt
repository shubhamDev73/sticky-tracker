package org.smoke.sticky.tracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sticky")
data class Sticky(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Float,
    val timeMillis: Long,
)
