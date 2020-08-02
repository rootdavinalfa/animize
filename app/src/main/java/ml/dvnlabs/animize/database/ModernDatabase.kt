/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [RecentPlayed::class], version = 1, exportSchema = false)
abstract class ModernDatabase : RoomDatabase() {
    abstract fun recentPlayedDAO(): RecentPlayedDAO

    companion object {
        @Volatile
        private var INSTANCE: ModernDatabase? = null

        fun getDatabase(
                context: Context
        ): ModernDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE
                    ?: synchronized(this) {
                        val instance = Room.databaseBuilder(
                                context.applicationContext,
                                ModernDatabase::class.java,
                                "modern_database.db"
                        )
                                .build()
                        INSTANCE = instance
                        // return instance
                        instance
                    }
        }
    }
}