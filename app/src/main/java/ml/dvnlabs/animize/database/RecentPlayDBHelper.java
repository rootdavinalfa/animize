package ml.dvnlabs.animize.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ml.dvnlabs.animize.database.model.recentland;

public class RecentPlayDBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = dbversion.DatabaseVer;

    // Database Name
    private static final String DATABASE_NAME = "local_animize_db";

    private ArrayList<recentland> model;

    public RecentPlayDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    //Function for adding recent played
    public void add_recent(String package_id,String package_name,String anmid,int episode,String url_cover,long timestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            //INSERT ALL DATA
            int index = check_lastindex();
            values.put(recentland.col_indexlist,index+1);
            values.put(recentland.col_packageid,package_id);
            values.put(recentland.col_packagename,package_name);
            values.put(recentland.col_anmid,anmid);
            values.put(recentland.col_episode,episode);
            values.put(recentland.col_urlcover,url_cover);
            values.put(recentland.col_lasttimestamp,timestamp);
            values.put(recentland.col_lastmodified,System.currentTimeMillis());

            db.insert(recentland.table_name,null,values);
            db.close();
        }catch (SQLiteException e){
            Log.e("Error Add: ",e.getMessage());
        }
    }
    //Function for updateing recent
    public void update_recent(String package_id,String package_name,String anmid,int episode,String url_cover,long timestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            //INSERT ALL DATA
            int index = check_lastindex();
            values.put(recentland.col_packageid,package_id);
            values.put(recentland.col_packagename,package_name);
            values.put(recentland.col_anmid,anmid);
            values.put(recentland.col_episode,episode);
            values.put(recentland.col_urlcover,url_cover);
            values.put(recentland.col_lasttimestamp,timestamp);
            values.put(recentland.col_lastmodified,System.currentTimeMillis());
            db.update(recentland.table_name,values," "+recentland.col_anmid+"='"+anmid+"'",null);
            db.close();
        }catch (SQLiteException e){
            Log.e("Error Add: ",e.getMessage());
        }
    }
    private int check_lastindex(){
        try {
            String lastquery = "SELECT  * FROM " + recentland.table_name+" ORDER BY "+recentland.col_indexlist+" DESC LIMIT 0,1";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(lastquery, null);
            int index = 0;
            if (cursor!=null && cursor.moveToFirst()){
                while (!cursor.isAfterLast()){
                    index = cursor.getInt(cursor.getColumnIndex(recentland.col_indexlist));
                    cursor.moveToNext();
                }
                cursor.close();
            }
            //db.close();
            return index;
        }catch (SQLiteException e){
            Log.e("Error Get: ",e.getMessage());
        }
        return 0;
    }
    //Function for get list of recent played
    public ArrayList<recentland> getrecentlist(){
        try {
            model = new ArrayList<>();
            long nowTimestamp = System.currentTimeMillis();
            //Max Days is 15 days
            long maxTimestamp = nowTimestamp - ((24 * 60 * 60 * 1000) * 15);
            Log.e("MAX:",String.valueOf(maxTimestamp));
            String selectquery = "SELECT * FROM "+recentland.table_name+" WHERE "+recentland.col_lastmodified+" >= "+maxTimestamp+" ORDER BY "+recentland.col_lastmodified+" DESC";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectquery,null);

            if (cursor!=null && cursor.moveToFirst()){
                while (!cursor.isAfterLast()){
                    String packageid = cursor.getString(cursor.getColumnIndex(recentland.col_packageid));
                    String packagename = cursor.getString(cursor.getColumnIndex(recentland.col_packagename));
                    String anmid = cursor.getString(cursor.getColumnIndex(recentland.col_anmid));
                    int episode = cursor.getInt(cursor.getColumnIndex(recentland.col_episode));
                    String cover = cursor.getString(cursor.getColumnIndex(recentland.col_urlcover));
                    long timestamp = cursor.getLong(cursor.getColumnIndex(recentland.col_lasttimestamp));
                    long modified = cursor.getLong(cursor.getColumnIndex(recentland.col_lastmodified));
                    model.add(new recentland(packageid,packagename,anmid,episode,cover,timestamp,modified));
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
    //function for read model recent played
    public recentland read_recent(String anmid){
        try {
            //model = new ArrayList<>();

            String selectquery = "SELECT * FROM "+recentland.table_name+" WHERE "+recentland.col_anmid+" = '"+anmid+"' ORDER BY "+recentland.col_lastmodified+" DESC";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectquery,null);
            recentland rec = new recentland();
            if (cursor!=null && cursor.moveToFirst()){
                while (!cursor.isAfterLast()){
                    String packageid = cursor.getString(cursor.getColumnIndex(recentland.col_packageid));
                    String packagename = cursor.getString(cursor.getColumnIndex(recentland.col_packagename));
                    int episode = cursor.getInt(cursor.getColumnIndex(recentland.col_episode));
                    String cover = cursor.getString(cursor.getColumnIndex(recentland.col_urlcover));
                    long timestamp = cursor.getLong(cursor.getColumnIndex(recentland.col_lasttimestamp));
                    long modified = cursor.getLong(cursor.getColumnIndex(recentland.col_lastmodified));
                    System.out.println("TIMESTAMP::"+timestamp);
                    //model.add(new recentland(packageid,packagename,anmid,episode,cover,timestamp));
                    rec = new recentland(packageid,packagename,anmid,episode,cover,timestamp,modified);
                    cursor.moveToNext();
                }

                cursor.close();
            }
            db.close();
            return rec;
        }catch (SQLiteException e){
            Log.e("Error get: ",e.getMessage());
        }
        return null;
    }

    public recentland read_recent_pn_package(String pkgid){
        try {
            //model = new ArrayList<>();

            String selectquery = "SELECT * FROM "+recentland.table_name+" WHERE "+recentland.col_packageid+" = '"+pkgid+"' ORDER BY "+recentland.col_lastmodified+" DESC LIMIT 0,1";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectquery,null);
            recentland rec = new recentland();
            if (cursor!=null && cursor.moveToFirst()){
                while (!cursor.isAfterLast()){
                    String packageid = cursor.getString(cursor.getColumnIndex(recentland.col_packageid));
                    String packagename = cursor.getString(cursor.getColumnIndex(recentland.col_packagename));
                    String anmid = cursor.getString(cursor.getColumnIndex(recentland.col_anmid));
                    int episode = cursor.getInt(cursor.getColumnIndex(recentland.col_episode));
                    //System.out.println(packagename);
                    String cover = cursor.getString(cursor.getColumnIndex(recentland.col_urlcover));
                    long timestamp = cursor.getLong(cursor.getColumnIndex(recentland.col_lasttimestamp));
                    long modified = cursor.getLong(cursor.getColumnIndex(recentland.col_lastmodified));
                    //System.out.println("TIMESTAMP::"+timestamp);
                    //model.add(new recentland(packageid,packagename,anmid,episode,cover,timestamp));
                    rec = new recentland(packageid,packagename,anmid,episode,cover,timestamp,modified);
                    cursor.moveToNext();
                }

                cursor.close();
            }
            db.close();
            return rec;
        }catch (SQLiteException e){
            Log.e("Error get: ",e.getMessage());
        }
        return null;
    }

    /*Check is anime_id is available on DB or not*/
    public boolean isRecentAvail(String anmid){
        try {
            String countQuery = "SELECT  * FROM " + recentland.table_name+" WHERE "+recentland.col_anmid+" = '"+anmid+"'";
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
    public boolean isRecentPackAvail(String pkgid){
        try {
            String countQuery = "SELECT  * FROM " + recentland.table_name+" WHERE "+recentland.col_packageid+" = '"+pkgid+"'";
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
    public boolean isRecentAvailLis(){
        try {
            String countQuery = "SELECT  * FROM " + recentland.table_name;
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
