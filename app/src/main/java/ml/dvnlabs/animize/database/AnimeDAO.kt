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
interface AnimeDAO {
    @Query("SELECT * FROM Anime")
    suspend fun getAllAnime(): List<Anime>

    @Query("SELECT * FROM Anime WHERE isStarred = 1")
    suspend fun getAllStarredAnime(): List<Anime>

    @Query("SELECT * FROM Anime WHERE packageID = :packageID")
    suspend fun getAnimeByPackageID(packageID: String): Anime?

    @Query("SELECT isStarred FROM Anime WHERE packageID = :packageID")
    suspend fun isAnimeStarred(packageID: String): Boolean

    @Query("UPDATE Anime SET isStarred = :starred WHERE packageID = :packageID")
    suspend fun changeStarred(packageID: String, starred: Boolean)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun newAnime(anime: Anime)

    @Query("UPDATE Anime SET episodeTotal = :episodeTotal, currentEpisode = :currentEpisode, updatedOn = :updatedOn, packageName = :packageName WHERE packageID = :packageID")
    suspend fun updateAnime(packageID: String, episodeTotal: Int, currentEpisode: Int, updatedOn: Long, packageName: String)

    @Query("DELETE FROM Anime")
    suspend fun deleteAll()
}