/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.model;

public class play_model {
    private String url;

    public play_model(String urli){
        this.url = urli;
    }

    public String getUrl() {
        return url;
    }
}
