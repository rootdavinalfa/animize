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

@Entity(indices = [Index(value = ["idUser", "email"], unique = true)])
data class User(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val idUser: String,
        val nameUser: String,
        val accessToken: String,
        val email: String
)