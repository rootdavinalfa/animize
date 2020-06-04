/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import ml.dvnlabs.animize.database.model.RecentLand
import java.util.*

class RecentPlayDBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private var model: ArrayList<RecentLand>? = null
    override fun onCreate(db: SQLiteDatabase) {}
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    //Function for adding recent played
    fun addRecent(package_id: String?, package_name: String?, anmid: String?, episode: Int, url_cover: String?, timestamp: Long,max : Long = 0) {
        val db = this.writableDatabase
        try {
            val values = ContentValues()
            //INSERT ALL DATA
            val index = checkLastIndex()
            values.put(RecentLand.col_indexlist, index + 1)
            values.put(RecentLand.col_packageid, package_id)
            values.put(RecentLand.col_packagename, package_name)
            values.put(RecentLand.col_anmid, anmid)
            values.put(RecentLand.col_episode, episode)
            values.put(RecentLand.col_urlcover, url_cover)
            values.put(RecentLand.col_lasttimestamp, timestamp)
            values.put(RecentLand.col_lastmodified, System.currentTimeMillis())
            values.put(RecentLand.col_maxTime,max)
            db.insert(RecentLand.table_name, null, values)
            db.close()
        } catch (e: SQLiteException) {
            Log.e("Error Add: ", e.message!!)
        }
    }

    //Function for updating recent
    fun updateRecent(package_id: String?, package_name: String?, anmid: String, episode: Int, url_cover: String?, timestamp: Long,max : Long = 0) {
        val db = this.writableDatabase
        try {
            val values = ContentValues()
            //INSERT ALL DATA
            val index = checkLastIndex()
            values.put(RecentLand.col_packageid, package_id)
            values.put(RecentLand.col_packagename, package_name)
            values.put(RecentLand.col_anmid, anmid)
            values.put(RecentLand.col_episode, episode)
            values.put(RecentLand.col_urlcover, url_cover)
            values.put(RecentLand.col_lasttimestamp, timestamp)
            values.put(RecentLand.col_lastmodified, System.currentTimeMillis())
            values.put(RecentLand.col_maxTime,max)
            db.update(RecentLand.table_name, values, " " + RecentLand.col_anmid + "='" + anmid + "'", null)
            db.close()
        } catch (e: SQLiteException) {
            Log.e("Error Add: ", e.message!!)
        }
    }

    private fun checkLastIndex(): Int {
        try {
            val lastquery = "SELECT  * FROM " + RecentLand.table_name + " ORDER BY " + RecentLand.col_indexlist + " DESC LIMIT 0,1"
            val db = this.readableDatabase
            val cursor = db.rawQuery(lastquery, null)
            var index = 0
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    index = cursor.getInt(cursor.getColumnIndex(RecentLand.col_indexlist))
                    cursor.moveToNext()
                }
                cursor.close()
            }
            //db.close();
            return index
        } catch (e: SQLiteException) {
            Log.e("Error Get: ", e.message!!)
        }
        return 0
    }

    //Function for get list of recent played
    fun getRecentList(): ArrayList<RecentLand>? {
        try {
            model = ArrayList()
            val nowTimestamp = System.currentTimeMillis()
            //Max Days is 15 days
            val maxTimestamp = nowTimestamp - 24 * 60 * 60 * 1000 * 15
            Log.e("MAX:", maxTimestamp.toString())
            val selectquery = "SELECT * FROM " + RecentLand.table_name + " WHERE " + RecentLand.col_lastmodified + " >= " + maxTimestamp + " ORDER BY " + RecentLand.col_lastmodified + " DESC"
            val db = this.readableDatabase
            val cursor = db.rawQuery(selectquery, null)
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val packageid = cursor.getString(cursor.getColumnIndex(RecentLand.col_packageid))
                    val packagename = cursor.getString(cursor.getColumnIndex(RecentLand.col_packagename))
                    val anmid = cursor.getString(cursor.getColumnIndex(RecentLand.col_anmid))
                    val episode = cursor.getInt(cursor.getColumnIndex(RecentLand.col_episode))
                    val cover = cursor.getString(cursor.getColumnIndex(RecentLand.col_urlcover))
                    val timestamp = cursor.getLong(cursor.getColumnIndex(RecentLand.col_lasttimestamp))
                    val modified = cursor.getLong(cursor.getColumnIndex(RecentLand.col_lastmodified))
                    val max = cursor.getLong(cursor.getColumnIndex(RecentLand.col_maxTime))
                    model!!.add(RecentLand(packageid, packagename, anmid, episode, cover, timestamp, modified, max))
                    cursor.moveToNext()
                }
                cursor.close()
            }
            db.close()
            return model
        } catch (e: SQLiteException) {
            Log.e("Error get: ", e.message!!)
        }
        return null
    }

    //function for read model recent played
    fun readRecent(anmid: String): RecentLand? {
        try {
            //model = new ArrayList<>();
            val selectquery = "SELECT * FROM " + RecentLand.table_name + " WHERE " + RecentLand.col_anmid + " = '" + anmid + "' ORDER BY " + RecentLand.col_lastmodified + " DESC"
            val db = this.readableDatabase
            val cursor = db.rawQuery(selectquery, null)
            var rec = RecentLand()
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val packageid = cursor.getString(cursor.getColumnIndex(RecentLand.col_packageid))
                    val packagename = cursor.getString(cursor.getColumnIndex(RecentLand.col_packagename))
                    val episode = cursor.getInt(cursor.getColumnIndex(RecentLand.col_episode))
                    val cover = cursor.getString(cursor.getColumnIndex(RecentLand.col_urlcover))
                    val timestamp = cursor.getLong(cursor.getColumnIndex(RecentLand.col_lasttimestamp))
                    val modified = cursor.getLong(cursor.getColumnIndex(RecentLand.col_lastmodified))
                    val max = cursor.getLong(cursor.getColumnIndex(RecentLand.col_maxTime))
                    rec = RecentLand(packageid, packagename, anmid, episode, cover, timestamp, modified,max)
                    cursor.moveToNext()
                }
                cursor.close()
                db.close()
                return rec
            }
            return null
        } catch (e: SQLiteException) {
            Log.e("Error get: ", e.message!!)
        }
        return null
    }

    fun readRecentOnPackage(pkgid: String): RecentLand? {
        try {
            //model = new ArrayList<>();
            val selectquery = "SELECT * FROM " + RecentLand.table_name + " WHERE " + RecentLand.col_packageid + " = '" + pkgid + "' ORDER BY " + RecentLand.col_lastmodified + " DESC LIMIT 0,1"
            val db = this.readableDatabase
            val cursor = db.rawQuery(selectquery, null)
            var rec = RecentLand()
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val packageid = cursor.getString(cursor.getColumnIndex(RecentLand.col_packageid))
                    val packagename = cursor.getString(cursor.getColumnIndex(RecentLand.col_packagename))
                    val anmid = cursor.getString(cursor.getColumnIndex(RecentLand.col_anmid))
                    val episode = cursor.getInt(cursor.getColumnIndex(RecentLand.col_episode))
                    //System.out.println(packagename);
                    val cover = cursor.getString(cursor.getColumnIndex(RecentLand.col_urlcover))
                    val timestamp = cursor.getLong(cursor.getColumnIndex(RecentLand.col_lasttimestamp))
                    val modified = cursor.getLong(cursor.getColumnIndex(RecentLand.col_lastmodified))
                    val max = cursor.getLong(cursor.getColumnIndex(RecentLand.col_maxTime))
                    //System.out.println("TIMESTAMP::"+timestamp);
                    //model.add(new recentland(packageid,packagename,anmid,episode,cover,timestamp));
                    rec = RecentLand(packageid, packagename, anmid, episode, cover, timestamp, modified,max)
                    cursor.moveToNext()
                }
                cursor.close()
            }
            db.close()
            return rec
        } catch (e: SQLiteException) {
            Log.e("Error get: ", e.message!!)
        }
        return null
    }

    /*Check is anime_id is available on DB or not*/
    fun isRecentAvail(anmid: String): Boolean {
        try {
            val countQuery = "SELECT  * FROM " + RecentLand.table_name + " WHERE " + RecentLand.col_anmid + " = '" + anmid + "'"
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)
            val count = cursor.count
            cursor.close()
            if (count > 0) {
                return true
            }
            db.close()
            return false
        } catch (e: SQLiteException) {
            Log.e("Error Get: ", e.message!!)
        }
        return false
    }

    fun isRecentPackAvail(pkgid: String): Boolean {
        try {
            val countQuery = "SELECT  * FROM " + RecentLand.table_name + " WHERE " + RecentLand.col_packageid + " = '" + pkgid + "'"
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)
            val count = cursor.count
            cursor.close()
            if (count > 0) {
                return true
            }
            db.close()
            return false
        } catch (e: SQLiteException) {
            Log.e("Error Get: ", e.message!!)
        }
        return false
    }

    val isRecentAvailLis: Boolean
        get() {
            try {
                val countQuery = "SELECT  * FROM " + RecentLand.table_name
                val db = this.readableDatabase
                val cursor = db.rawQuery(countQuery, null)
                val count = cursor.count
                cursor.close()
                if (count > 0) {
                    return true
                }
                db.close()
                return false
            } catch (e: SQLiteException) {
                Log.e("Error Get: ", e.message!!)
            }
            return false
        }

    companion object {
        // Database Version
        private const val DATABASE_VERSION = DBVersion.DatabaseVer

        // Database Name
        private const val DATABASE_NAME = "local_animize_db"
    }
}