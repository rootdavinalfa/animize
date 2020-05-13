/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.pager;

import java.util.ArrayList;

import ml.dvnlabs.animize.model.VideoPlayModel;

public interface passdata_arraylist {
    void onDataReceived(ArrayList<VideoPlayModel> data, String id);
}
