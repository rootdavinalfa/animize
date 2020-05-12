/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.model;

public class starmodel {
    private String packageid,name,total_ep,rating,mal,cover;

    public starmodel(String packageid, String name, String total_ep, String rating, String mal, String cover) {
        this.packageid = packageid;
        this.name = name;
        this.total_ep = total_ep;
        this.rating = rating;
        this.mal = mal;
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }

    public String getPackageid() {
        return packageid;
    }

    public String getMal() {
        return mal;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public String getTotal_ep() {
        return total_ep;
    }

    public void setPackageid(String packageid) {
        this.packageid = packageid;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setMal(String mal) {
        this.mal = mal;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setTotal_ep(String total_ep) {
        this.total_ep = total_ep;
    }
}
