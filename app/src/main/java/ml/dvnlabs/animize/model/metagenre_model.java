/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.model;

public class metagenre_model {
    private String title,count;

    public metagenre_model(String title, String count) {
        this.title = title;
        this.count = count;
    }

    public String getCount() {
        return count;
    }

    public String getTitle() {
        return title;
    }
}
