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
interface RecentPlayedDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun newRecent(recent: RecentPlayed)

    @Query("UPDATE RecentPlayed SET episode = :episode , urlCover = :urlCover ,timestamp = :timestamp,duration = :max , modified = :modified WHERE packageID = :packageID AND animeID = :anmid")
    suspend fun updateRecent(packageID: String, anmid: String, episode: Int, urlCover: String, timestamp: Long, max: Long = 0, modified: Long = System.currentTimeMillis())

    @Query("SELECT * FROM RecentPlayed ORDER BY modified DESC")
    suspend fun getRecentList(): List<RecentPlayed>

    @Query("SELECT * FROM RecentPlayed WHERE animeID = :animeID ORDER BY modified LIMIT 0,1")
    suspend fun getRecentByAnimeID(animeID: String): RecentPlayed?

    @Query("SELECT * FROM RecentPlayed WHERE packageID = :packageID ORDER BY modified LIMIT 0,1")
    suspend fun getRecentByPackageID(packageID: String): RecentPlayed?

    @Query("SELECT * FROM RecentPlayed WHERE packageID = :packageID ORDER BY modified DESC LIMIT 0,:max")
    suspend fun getLimitedRecentByPackageID(packageID: String, max: Int = 5): RecentPlayed?

    @Query("DELETE FROM RecentPlayed")
    suspend fun deleteAll()

}