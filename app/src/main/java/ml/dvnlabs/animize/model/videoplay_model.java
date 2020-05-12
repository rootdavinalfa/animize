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

public class videoplay_model {
    private String name_anim,episode,total_ep_anim,rating,sysnop,pack,source_url,url_thmb,cover;
    private List<String> genres;


    public videoplay_model(String name, String ep, String total, String rate, String synop, String pkg, String url, List<String> genre, String url_thm, String cover) {
        this.episode = ep;
        this.name_anim = name;
        this.rating = rate;
        this.source_url = url;
        this.sysnop = synop;
        this.total_ep_anim = total;
        this.pack = pkg;
        this.genres = genre;
        this.url_thmb = url_thm;
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }

    public String getUrl_thmb() {
        return url_thmb;
    }

    public String getSource_url() {
        return source_url;
    }

    public String getEpisode() {
        return episode;
    }

    public String getName_anim() {
        return name_anim;
    }

    public String getRating() {
        return rating;
    }

    public String getSysnop() {
        return sysnop;
    }

    public String getTotal_ep_anim() {
        return total_ep_anim;
    }

    public String getPack() {
        return pack;
    }

    public List<String> getGenres() {
        return this.genres;
    }
}
