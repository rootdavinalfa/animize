/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.database.notification

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "starred_notification", indices = [Index(value = ["anm_id"], unique = true)])
data class StarredNotification(
        @PrimaryKey(autoGenerate = true) var uid: Long = 0,
        @ColumnInfo(name = "name_catalogue") var nameCatalogue: String,
        @ColumnInfo(name = "pkg_id") var packageID: String,
        @ColumnInfo(name = "anm_id") var animeID: String,
        @ColumnInfo(name = "episode") var episode: Int = 1,
        @ColumnInfo(name = "thumbnail") var thumbnailURL: String,
        @ColumnInfo(name = "opened") var opened: Boolean = false,
        @ColumnInfo(name = "notification_posted") var posted: Boolean = false,
        @ColumnInfo(typeAffinity = ColumnInfo.INTEGER) var syncTime: Long = 0
) {
    constructor() : this(0, "", "", "", 1, "", false, false, 0)
}