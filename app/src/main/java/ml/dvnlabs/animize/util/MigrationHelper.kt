/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.util

import android.content.Context
import android.content.SharedPreferences
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import ml.dvnlabs.animize.database.Anime
import ml.dvnlabs.animize.database.AnimizeDatabase
import ml.dvnlabs.animize.database.RecentPlayed
import ml.dvnlabs.animize.database.User
import ml.dvnlabs.animize.database.legacy.InitInternalDBHelper
import ml.dvnlabs.animize.database.legacy.PackageStarDBHelper
import ml.dvnlabs.animize.database.legacy.RecentPlayDBHelper
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

/**
 * This class intended for usage to migrate from legacy database file to the RoomDB
 * Please don't call again after migrated
 */
class MigrationHelper(val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams), KoinComponent {
    private lateinit var initDB: InitInternalDBHelper
    private lateinit var packageStar: PackageStarDBHelper
    private lateinit var recentPlay: RecentPlayDBHelper
    private val animizeDB: AnimizeDatabase by inject { parametersOf(context) }
    private var migratedPref: SharedPreferences = context.getSharedPreferences("dbStatus", Context.MODE_PRIVATE)

    override suspend fun doWork(): Result {
        initDB = InitInternalDBHelper(context)
        packageStar = PackageStarDBHelper(context)
        recentPlay = RecentPlayDBHelper(context)
        val migrated = migratedPref.getBoolean("migrated", false)
        if (!migrated) {
            migrateStarred()
            migrateUser()
            migrateRecentPlayed()
            with(migratedPref.edit()) {
                this.putBoolean("migrated", true)
                apply()
            }
        }
        return Result.success()
    }

    //Migrate user then delete from legacy
    private suspend fun migrateUser() {
        if (initDB.userCount) {
            val user = initDB.user
            user?.let {
                animizeDB.userDAO().newUser(User(
                        idUser = it.idUser!!,
                        nameUser = it.nameUser!!,
                        email = it.email!!,
                        accessToken = it.token!!
                ))
            }
        }
    }

    private suspend fun migrateRecentPlayed() {
        val recent = recentPlay.getRecentList()
        recent?.let {
            for (i in it) {
                if (animizeDB.animeDAO().getAnimeByPackageID(i.packageId!!) == null) {
                    animizeDB.animeDAO().newAnime(Anime(
                            packageID = i.packageId!!,
                            packageName = null
                    ))
                }
                animizeDB.recentPlayedDAO().newRecent(RecentPlayed(
                        packageID = i.packageId!!,
                        name = i.packageName!!,
                        urlCover = i.urlCover!!,
                        animeID = i.anmid!!,
                        timestamp = i.timestamp,
                        modified = i.modified,
                        duration = i.maxTime,
                        episode = i.episode
                ))
            }
        }
    }

    private suspend fun migrateStarred() {
        val starred = packageStar.starredList
        starred?.let {
            for (i in it) {
                if (animizeDB.animeDAO().getAnimeByPackageID(i.packageid) == null) {
                    animizeDB.animeDAO().newAnime(Anime(
                            packageID = i.packageid,
                            packageName = null,
                            isStarred = true
                    ))
                } else {
                    animizeDB.animeDAO().changeStarred(i.packageid, true)
                }
                animizeDB.animeDAO().updateAnime(packageID = i.packageid, updatedOn = System.currentTimeMillis())
            }
        }
    }


    companion object {
        const val TAG = "MigrationHelper"
        fun setupTaskImmediately(context: Context) {
            val request = OneTimeWorkRequestBuilder<MigrationHelper>()
                    .addTag(TAG)
                    .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}

interface migrationListener {
    fun onMigrated()
}