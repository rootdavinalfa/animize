/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun newUser(user: User)

    @Query("SELECT * FROM User LIMIT 0,1")
    suspend fun getUser(): User

    @Query("DELETE FROM User")
    suspend fun deleteUser()

    @Query("SELECT COUNT(*) FROM User")
    suspend fun countUser(): Int
}