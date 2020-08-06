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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [RecentPlayed::class, Anime::class, User::class], version = 2, exportSchema = false)
abstract class AnimizeDatabase : RoomDatabase() {
    abstract fun recentPlayedDAO(): RecentPlayedDAO
    abstract fun userDAO(): UserDAO
    abstract fun animeDAO(): AnimeDAO

    companion object {
        @Volatile
        private var INSTANCE: AnimizeDatabase? = null

        fun getDatabase(
                context: Context
        ): AnimizeDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE
                    ?: synchronized(this) {
                        val instance = Room.databaseBuilder(
                                context.applicationContext,
                                AnimizeDatabase::class.java,
                                "animize.db"
                        ).addMigrations(migrate1To2)
                                .build()
                        INSTANCE = instance
                        // return instance
                        instance
                    }
        }

        val migrate1To2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Anime ADD COLUMN starredOn INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}