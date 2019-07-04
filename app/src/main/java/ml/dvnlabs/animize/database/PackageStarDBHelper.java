package ml.dvnlabs.animize.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ml.dvnlabs.animize.database.model.starland;

public class PackageStarDBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "local_animize_db";

    private ArrayList<starland> model;

    public PackageStarDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(starland.CREATE_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + starland.table_name);

        // Create tables again
        onCreate(db);
    }


    //Function for add to library
    public void add_star(String packageid){
        SQLiteDatabase db = this.getWritableDatabase();
        try{

            ContentValues values = new ContentValues();
            //INSERT ALL DATA

            values.put(starland.col_package_id,packageid);

            System.out.println("INSERTED: "+packageid);
            //long id_db = db.insert(userland.table_name,null,values);
            db.insert(starland.table_name,null,values);
            db.close();
        }catch (SQLiteException e){
            Log.e("Error Add: ",e.getMessage());
        }

         
    }

    //Function to show list to library
    public ArrayList<starland> getStarredList(){
        try {
            model = new ArrayList<>();

            String selectquery = "SELECT * FROM "+starland.table_name+" ORDER BY "+starland.col_indexlist+" DESC";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectquery,null);

            if (cursor!=null && cursor.moveToFirst()){
                while (!cursor.isAfterLast()){
                    String packageid = cursor.getString(cursor.getColumnIndex(starland.col_package_id));
                    model.add(new starland(packageid));
                    cursor.moveToNext();
                }

                cursor.close();
            }
            db.close();
            return model;
        }catch (SQLiteException e){
            Log.e("Error get: ",e.getMessage());
        }
        return null;

    }

    //Unstar package
    public void unStar(String packageid){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(starland.table_name,starland.col_package_id+"= '" + packageid+"'",null);
            db.close();
        }catch (SQLiteException e){
            Log.e("Error Delete: ",e.getMessage());
        }

         
    }

    //Check if package starred
    public boolean isStarred(String packageid){
        try {
            String countQuery = "SELECT  * FROM " + starland.table_name+" WHERE "+starland.col_package_id+" = '"+packageid+"'";
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
            Log.e("Error Get: ",e.getMessage());
        }
        return false;
    }


    //Function for check for list count
    public boolean isAvail(){
        try {
            String countQuery = "SELECT  * FROM " + starland.table_name;
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
            Log.e("Error Get: ",e.getMessage());
        }

        return false;
    }
}
