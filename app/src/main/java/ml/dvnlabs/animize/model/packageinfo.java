/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.model;

import java.util.List;

public class packageinfo {
    private String pack, name, syn, tot, rate, mal, cover;
    List<String>genre;

    public packageinfo(String pack, String name, String sysnop, String tot, String rate, String mal, List<String> genre, String cover) {
        this.pack = pack;
        this.name = name;
        this.syn = sysnop;
        this.tot= tot;
        this.rate = rate;
        this.mal = mal;
        this.genre = genre;
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }

    public String getMal() {
        return mal;
    }

    public List<String> getGenre() {
        return this.genre;
    }

    public String getname() {
        return name;
    }

    public String getSynopsis() {
        return syn;
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
