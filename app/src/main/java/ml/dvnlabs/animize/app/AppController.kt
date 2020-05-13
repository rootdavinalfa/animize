/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.app

import android.app.Application
import android.content.Context
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import io.branch.referral.Branch
import ml.dvnlabs.animize.checker.CheckNetwork
import ml.dvnlabs.animize.checker.CheckNetwork.ConnectivityReceiverListener
import java.io.File
import java.io.IOException

class AppController : Application() {
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        // Initialize the Branch object
        Branch.getAutoInstance(this)
    }

    fun setConnectivityListener(listener: ConnectivityReceiverListener?) {
        CheckNetwork.connectivityReceiverListener = listener
    }

    companion object {
        val TAG = AppController::class.java
                .simpleName
        private var sDownloadCache: SimpleCache? = null
        var max_cache_size = 1024 * 1024 * 40.toLong()
        private var mInstance: AppController? = null

        @JvmStatic
        @get:Synchronized
        val instance: AppController?
            get() {
                if (mInstance == null) {
                    mInstance = AppController()
                }
                return mInstance
            }

        fun isDebug(context: Context): Boolean {
            //Check is PACKAGE_NAME is debug or not,if not return false;otherwise return true if release version
            return context.packageName == "ml.dvnlabs.animize.ima.debug"
        }

        fun setVideoCache(): SimpleCache? {
            if (sDownloadCache == null) {
                val databaseProvider: DatabaseProvider = ExoDatabaseProvider(mInstance!!)
                sDownloadCache = SimpleCache(File(mInstance!!.cacheDir, "anim"), LeastRecentlyUsedCacheEvictor(max_cache_size),databaseProvider) //17MB
            }
            return sDownloadCache
        }

        @Throws(IOException::class)
        private fun cleanDirectory(file: File) {
            if (!file.exists()) {
                return
            }
            val contentFiles = file.listFiles()
            if (contentFiles != null) {
                for (contentFile in contentFiles) {
                    delete(contentFile)
                }
            }
        }

        @Throws(IOException::class)
        private fun delete(file: File) {
            if (file.isFile && file.exists()) {
                deleteOrThrow(file)
            } else {
                cleanDirectory(file)
                deleteOrThrow(file)
            }
        }

        @Throws(IOException::class)
        private fun deleteOrThrow(file: File) {
            if (file.exists()) {
                val isDeleted = file.delete()
                if (!isDeleted) {
                    throw IOException(String.format("File %s can't be deleted", file.absolutePath))
                }
            }
        }
    }
}