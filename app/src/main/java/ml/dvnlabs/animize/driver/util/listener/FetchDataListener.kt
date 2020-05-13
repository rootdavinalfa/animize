/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.driver.util.listener

interface FetchDataListener {
    fun onFetchComplete(data: String?)
    fun onFetchFailure(msg: String?)
    fun onFetchStart()
}