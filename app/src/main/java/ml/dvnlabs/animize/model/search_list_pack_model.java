/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.model;

public class search_list_pack_model {
    private String pkgid,title,rating,now,tot,cover;

    public search_list_pack_model(String pkgid, String title, String now, String total, String rating, String cover) {
        this.pkgid = pkgid;
        this.title = title;
        this.now = now;
        this.tot = total;
        this.rating = rating;
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }

    public String getPkgid() {
        return pkgid;
    }

    public String getTitle() {
        return title;
    }

    public String getRating() {
        return rating;
    }

    public String getTot() {
        return tot;
    }

    public String getNow() {
        return now;
    }
}
