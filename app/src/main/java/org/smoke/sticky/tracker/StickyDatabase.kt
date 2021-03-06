package org.smoke.sticky.tracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.smoke.sticky.tracker.model.Sticky
import org.smoke.sticky.tracker.model.StickyDao

@Database(entities = [Sticky::class], version = 2, exportSchema = false)
abstract class StickyDatabase: RoomDatabase() {
    abstract fun stickyDao(): StickyDao

    companion object {
        @Volatile
        private var INSTANCE: StickyDatabase? = null
        fun getDatabase(context: Context): StickyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StickyDatabase::class.java,
                    "sticky_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}