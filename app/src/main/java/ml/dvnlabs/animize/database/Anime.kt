/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["packageID"], unique = true)])
data class Anime(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val packageID: String,
        val packageName: String?,
        var episodeTotal: Int = 0,
        var currentEpisode: Int = 0,
        var updatedOn: Long = 0,
        var isStarred: Boolean = false,
        var starredOn: Long = 0 // Add on database version 2
)