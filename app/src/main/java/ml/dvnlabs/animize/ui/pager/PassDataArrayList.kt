/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.pager

import ml.dvnlabs.animize.model.VideoPlayModel
import java.util.*

interface PassDataArrayList {
    fun onDataReceived(data: ArrayList<VideoPlayModel?>?, id: String?)
}