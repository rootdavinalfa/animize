/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.database.model

class StarLand(var packageid: String) {
    var ind: String? = null

    companion object {
        const val table_name = "star_package"
        const val col_package_id = "package_id"
        const val col_indexlist = "indexlist"
        const val CREATE_TABLE = "CREATE TABLE " + table_name + "(" + col_indexlist + " INTEGER PRIMARY KEY, " +
                col_package_id + " TEXT)"
    }

}