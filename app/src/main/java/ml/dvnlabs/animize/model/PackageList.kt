/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.model

class PackageList(val pack: String, private val name: String, val now: String, val tot: String, val rate: String, val mal: String, var genre: List<String>?, val coverUrl: String) {

    fun getName(): String {
        return name
    }

}