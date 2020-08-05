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
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["animeID"], unique = true), Index(value = ["packageID"])],
        foreignKeys = [ForeignKey(
                entity = Anime::class,
                parentColumns = ["packageID"],
                childColumns = ["packageID"],
                onDelete = ForeignKey.CASCADE)])

data class RecentPlayed(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val packageID: String,
        val animeID: String,
        val name: String,
        var urlCover: String,
        var timestamp: Long = 0,
        var episode: Int = 0,
        var modified: Long = 0,
        var duration: Long = 0
)