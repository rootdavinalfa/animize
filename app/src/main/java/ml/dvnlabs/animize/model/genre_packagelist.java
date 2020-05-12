/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.model;

public class genre_packagelist {
    private String pack, name, now, tot, rate, mal, cover;

    public genre_packagelist(String pack, String name, String now, String tot, String rate, String mal, String cover) {
        this.pack = pack;
        this.name = name;
        this.now = now;
        this.tot= tot;
        this.rate = rate;
        this.mal = mal;
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }

    public String getMal() {
        return mal;
    }


    public String getname() {
        return name;
    }

    public String getNow() {
        return now;
    }

    public String getPack() {
        return pack;
    }

    public String getRate() {
        return rate;
    }

    public String getTot() {
        return tot;
    }
}
