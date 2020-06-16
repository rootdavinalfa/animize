/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.database.notification

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StarredNotification::class], version = 1,exportSchema = false)
abstract class StarredNotificationDatabase : RoomDatabase() {
    abstract fun starredNotificationDAO(): StarredNotificationDAO

    companion object {
        @Volatile
        private var INSTANCE: StarredNotificationDatabase? = null

        fun getDatabase(
                context: Context
        ): StarredNotificationDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE
                    ?: synchronized(this) {
                        val instance = Room.databaseBuilder(
                                context.applicationContext,
                                StarredNotificationDatabase::class.java,
                                "starred_notification.db"
                        )
                                .build()
                        INSTANCE = instance
                        // return instance
                        instance
                    }
        }
    }
}