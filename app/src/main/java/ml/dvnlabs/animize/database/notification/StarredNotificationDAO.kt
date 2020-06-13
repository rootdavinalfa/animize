/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.database.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StarredNotificationDAO {
    @Query("SELECT COUNT(*) FROM starred_notification WHERE pkg_id = :pkkID")
    suspend fun checkPKGID(pkkID: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun newNotification(starredNotification: StarredNotification)

    @Query("DELETE FROM starred_notification WHERE pkg_id = :pkgID")
    suspend fun deletePKGID(pkgID: String)

    @Query("DELETE FROM starred_notification")
    suspend fun deleteALL()


    @Query("SELECT * FROM starred_notification ORDER BY uid DESC")
    suspend fun getStarredNotificationList(): List<StarredNotification>

    @Query("SELECT * FROM starred_notification WHERE opened = 0 ORDER BY uid DESC")
    suspend fun getStarredNotificationListNotOpened(): List<StarredNotification>

    @Query("SELECT * FROM starred_notification WHERE opened = 0 AND notification_posted = 0 ORDER BY uid DESC")
    suspend fun getStarredNotificationListNotOpenedAndNotPosted(): List<StarredNotification>

    @Query("UPDATE starred_notification SET opened = 1 WHERE anm_id = :anmID")
    suspend fun notificationOpened(anmID: String)

    @Query("UPDATE starred_notification SET notification_posted = 1 WHERE anm_id= :anmID")
    suspend fun notificationPosted(anmID: String)
}