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
import ml.dvnlabs.animize.database.model.StarLand
import java.util.*

class PackageStarDBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private var model: ArrayList<StarLand>? = null

    // Creating Tables
    override fun onCreate(db: SQLiteDatabase) {

        // create notes table
        //db.execSQL(starland.CREATE_TABLE);
    }

    // Upgrading database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + starland.table_name);

        // Create tables again
        //onCreate(db);
    }

    //Function for add to library
    fun addStar(packageid: String) {
        val db = this.writableDatabase
        try {
            val values = ContentValues()
            //INSERT ALL DATA
            values.put(StarLand.col_package_id, packageid)
            println("INSERTED: $packageid")
            //long id_db = db.insert(userland.table_name,null,values);
            db.insert(StarLand.table_name, null, values)
            db.close()
        } catch (e: SQLiteException) {
            Log.e("Error Add: ", e.message!!)
        }
    }

    //Function to show list to library
    val starredList: ArrayList<StarLand>?
        get() {
            try {
                model = ArrayList()
                val selectquery = "SELECT * FROM " + StarLand.table_name + " ORDER BY " + StarLand.col_indexlist + " DESC"
                val db = this.readableDatabase
                val cursor = db.rawQuery(selectquery, null)
                if (cursor != null && cursor.moveToFirst()) {
                    while (!cursor.isAfterLast) {
                        val packageid = cursor.getString(cursor.getColumnIndex(StarLand.col_package_id))
                        model!!.add(StarLand(packageid))
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

    //Unstar package
    fun unStar(packageID: String) {
        try {
            val db = this.writableDatabase
            db.delete(StarLand.table_name, StarLand.col_package_id + "= '" + packageID + "'", null)
            db.close()
        } catch (e: SQLiteException) {
            Log.e("Error Delete: ", e.message!!)
        }
    }

    //Check if package starred
    fun isStarred(packageid: String): Boolean {
        try {
            val countQuery = "SELECT  * FROM " + StarLand.table_name + " WHERE " + StarLand.col_package_id + " = '" + packageid + "'"
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

    //Function for check for list count
    val isAvail: Boolean
        get() {
            try {
                val countQuery = "SELECT  * FROM " + StarLand.table_name
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