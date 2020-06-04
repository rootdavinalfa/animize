/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.database.model

class RecentLand {
    private var packageId: String? = null
        private set
    var packageName: String? = null
        private set
    var anmid: String? = null
        private set
    var urlCover: String? = null
        private set
    var timestamp: Long = 0
        private set
    var modified: Long = 0
        private set
    var maxTime: Long = 0
        private set
    var episode = 0
        private set


    constructor(package_id: String?, package_name: String?, anmid: String?, episode: Int, url_cover: String?, timestamp: Long, modified: Long,maxTime : Long = 0) {
        this.packageId = package_id
        this.anmid = anmid
        this.packageName = package_name
        this.urlCover = url_cover
        this.timestamp = timestamp
        this.episode = episode
        this.modified = modified
        this.maxTime = maxTime
    }

    constructor() {}

    companion object {
        const val table_name = "recentland"
        const val col_indexlist = "indexlist"
        const val col_packageid = "package_id"
        const val col_packagename = "package_name"
        const val col_anmid = "anm_id"
        const val col_episode = "episode"
        const val col_urlcover = "url_cover"
        const val col_lasttimestamp = "timestamp"
        const val col_lastmodified = "lastmodified"
        const val col_maxTime = "max_time"
        const val CREATE_TABLE = "CREATE TABLE " + table_name + "(" + col_indexlist + " INTEGER PRIMARY KEY, " +
                col_packageid + " TEXT, " + col_packagename + " TEXT, " + col_anmid + " TEXT, " + col_episode + " INTEGER, " + col_urlcover + " TEXT, " +
                "" + col_lasttimestamp + " INTEGER, " + col_lastmodified + " INTEGER, "+ col_maxTime+" INTEGER)"
    }
}