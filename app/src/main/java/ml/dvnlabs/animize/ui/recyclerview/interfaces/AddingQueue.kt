/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.recyclerview.interfaces

import ml.dvnlabs.animize.ui.recyclerview.packagelist.StarListAdapter
import java.util.*

interface AddingQueue {
    fun addQueue(pkganim: String, position: Int)
    fun removeQueue(position: Int)
    val queue: ArrayList<StarListAdapter.requestQueue?>?
}