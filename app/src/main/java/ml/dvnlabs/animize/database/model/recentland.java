/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.database.model;

public class recentland {
    public static final String table_name = "recentland";
    public static final String col_indexlist ="indexlist";
    public static final String col_packageid = "package_id";
    public static final String col_packagename = "package_name";
    public static final String col_anmid = "anm_id";
    public static final String col_episode = "episode";
    public static final String col_urlcover = "url_cover";
    public static final String col_lasttimestamp = "timestamp";
    public static final String col_lastmodified = "lastmodified";


    private String package_id,package_name,anmid,url_cover;
    private long timestamp,modified;
    private int episode;

    public static final String CREATE_TABLE = "CREATE TABLE " +table_name+"("+col_indexlist+" INTEGER PRIMARY KEY, "+
            col_packageid + " TEXT, "+col_packagename+" TEXT, "+col_anmid+" TEXT, "+col_episode+" INTEGER, "+col_urlcover+" TEXT, " +
            ""+col_lasttimestamp+" INTEGER, "+col_lastmodified+" INTEGER)";

    public recentland(String package_id, String package_name, String anmid, int episode, String url_cover, long timestamp, long modified){
        this.package_id = package_id;
        this.anmid = anmid;
        this.package_name = package_name;
        this.url_cover = url_cover;
        this.timestamp = timestamp;
        this.episode = episode;
        this.modified = modified;
    }

    public recentland(){}

    public int getEpisode() {
        return episode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getModified() {
        return modified;
    }

    public String getAnmid() {
        return anmid;
    }

    public String getPackage_id() {
        return package_id;
    }

    public String getPackage_name() {
        return package_name;
    }

    public String getUrl_cover() {
        return url_cover;
    }
}
