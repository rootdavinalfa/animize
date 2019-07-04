package ml.dvnlabs.animize.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ml.dvnlabs.animize.database.model.starland;
import ml.dvnlabs.animize.database.model.userland;

public class InitInternalDBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "local_animize_db";

    private ArrayList<starland> model;


    public InitInternalDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(userland.CREATE_TABLE);
        db.execSQL(starland.CREATE_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + userland.table_name);
        db.execSQL("DROP TABLE IF EXISTS " + starland.table_name);

        // Create tables again
        onCreate(db);
    }
    public void insertuser(String token,String idus,String ema,String nameus){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            //INSERT ALL DATA

            values.put(userland.col_id_user,idus);
            values.put(userland.col_name_user,nameus);
            values.put(userland.col_email,ema);
            values.put(userland.col_access_token,token);

            //long id_db = db.insert(userland.table_name,null,values);
            db.insert(userland.table_name,null,values);
            db.close();
        }catch (SQLiteException e){
            e.printStackTrace();
        }

        //return id_db;
    }
    public String sign_out(){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(userland.table_name,null,null);
            String status = "signout";
            db.close();
            return status;
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return null;

    }
    public userland getUser(){
        try {
            String selectquery = "SELECT * FROM "+userland.table_name+" ";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectquery,null);

            //if(cursor!=null)
            //cursor.moveToFirst();
            userland userlandd = new userland();
            if(cursor!=null && cursor.moveToFirst()){
                userlandd.setEmail(cursor.getString(cursor.getColumnIndex(userland.col_email)));
                userlandd.setIdUser(cursor.getString(cursor.getColumnIndex(userland.col_id_user)));
                userlandd.setNameUser(cursor.getString(cursor.getColumnIndex(userland.col_name_user)));
                userlandd.setToken(cursor.getString(cursor.getColumnIndex(userland.col_access_token)));
                cursor.close();
            }
            db.close();
            return userlandd;

        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return null;

    }
    public boolean getUserCount() {
        try {
            String countQuery = "SELECT  * FROM " + userland.table_name;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);

            int count = cursor.getCount();
            cursor.close();
            if(count>0){
                return true;
            }
            db.close();
            return false;
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return false;
        // return count
    }
}