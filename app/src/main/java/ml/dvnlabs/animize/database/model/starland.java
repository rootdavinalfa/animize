/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.database.model;

public class starland {
    public static final String table_name ="star_package";
    public static final String col_package_id ="package_id";
    public static final String col_indexlist ="indexlist";

    private String ind;
    private String packageid;

    public static final String CREATE_TABLE = "CREATE TABLE " +table_name+"("+col_indexlist+" INTEGER PRIMARY KEY, "+
            col_package_id + " TEXT)";

    public starland(String packageid){

        this.packageid = packageid;
    }

    public void setInd(String ind) {
        this.ind = ind;
    }

    public void setPackageid(String packageid) {
        this.packageid = packageid;
    }

    public String getInd() {
        return ind;
    }

    public String getPackageid() {
        return packageid;
    }
}
