/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.database.legacy

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import ml.dvnlabs.animize.constant.DBVersion
import ml.dvnlabs.animize.database.legacy.model.RecentLand
import ml.dvnlabs.animize.database.legacy.model.StarLand
import ml.dvnlabs.animize.database.legacy.model.UserLand
import java.util.*

class InitInternalDBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val model: ArrayList<StarLand>? = null

    // Creating Tables
    override fun onCreate(db: SQLiteDatabase) {

        // create notes table
        db.execSQL(UserLand.CREATE_TABLE)
        db.execSQL(StarLand.CREATE_TABLE)
        db.execSQL(RecentLand.CREATE_TABLE)
    }

    // Upgrading database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            if (checkTableNotExist(db, "star_package")) {
                db.execSQL(StarLand.CREATE_TABLE)
            }
        }
        if (oldVersion < 3) {
            if (checkTableNotExist(db, RecentLand.table_name)) {
                db.execSQL(RecentLand.CREATE_TABLE)
            }
        }
        if (oldVersion == 3 && newVersion == 4) {
            db.execSQL("ALTER TABLE ${RecentLand.table_name} ADD COLUMN ${RecentLand.col_maxTime} INTEGER DEFAULT 0")
        }


        // Create tables again
        //onCreate(db);
    }

    private fun checkTableNotExist(db: SQLiteDatabase, table_name: String): Boolean {
        val cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + table_name + "'", null)
        cursor.close()
        return cursor.count == 0
    }

    fun insertUser(token: String?, idus: String?, ema: String?, nameus: String?) {
        try {
            val db = this.writableDatabase
            val values = ContentValues()
            //INSERT ALL DATA
            values.put(UserLand.col_id_user, idus)
            values.put(UserLand.col_name_user, nameus)
            values.put(UserLand.col_email, ema)
            values.put(UserLand.col_access_token, token)

            //long id_db = db.insert(userland.table_name,null,values);
            db.insert(UserLand.table_name, null, values)
            db.close()
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    fun deleteUser() {
        try {
            val db = this.writableDatabase
            db.delete(UserLand.table_name, null, null)
            db.close()
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    fun signOut(): String? {
        try {
            val db = this.writableDatabase
            db.delete(UserLand.table_name, null, null)
            val status = "signout"
            db.close()
            deleteStarred()
            return status
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
        return null
    }

    private fun deleteStarred(): String? {
        try {
            val db = this.writableDatabase
            db.delete(StarLand.table_name, null, null)
            val status = "deleted"
            db.close()
            return status
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
        return null
    }

    val user: UserLand?
        get() {
            try {
                val selectQuery = "SELECT * FROM " + UserLand.table_name + " "
                val db = this.readableDatabase
                val cursor = db.rawQuery(selectQuery, null)

                //if(cursor!=null)
                //cursor.moveToFirst();
                var userland: UserLand? = null
                if (cursor != null && cursor.moveToFirst()) {
                    val email = cursor.getString(cursor.getColumnIndex(UserLand.col_email))
                    val idUser = cursor.getString(cursor.getColumnIndex(UserLand.col_id_user))
                    val nameUser = cursor.getString(cursor.getColumnIndex(UserLand.col_name_user))
                    val token = cursor.getString(cursor.getColumnIndex(UserLand.col_access_token))
                    userland = UserLand(idUser, nameUser, email, token)
                    cursor.close()
                }
                db.close()
                return userland
            } catch (e: SQLiteException) {
                e.printStackTrace()
            }
            return null
        }

    // return count
    val userCount: Boolean
        get() {
            try {
                val countQuery = "SELECT  * FROM " + UserLand.table_name
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
                e.printStackTrace()
            }
            return false
            // return count
        }

    companion object {
        // Database Version
        private const val DATABASE_VERSION = DBVersion.DatabaseVer

        // Database Name
        private const val DATABASE_NAME = "local_animize_db"
    }
}