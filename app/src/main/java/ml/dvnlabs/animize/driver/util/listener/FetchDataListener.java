/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.driver.util.listener;

public interface FetchDataListener {
    void onFetchComplete(String data);

    void onFetchFailure(String msg);

    void onFetchStart();
}
