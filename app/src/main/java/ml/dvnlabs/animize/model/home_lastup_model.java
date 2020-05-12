/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.model;

public class home_lastup_model {
    private String url_imagetitle;
    private String title_nm;
    private String ep_num;
    private String idn;

    public home_lastup_model(String ur, String id, String tn, String en) {
        this.ep_num = en;
        this.title_nm = tn;
        this.url_imagetitle = ur;
        this.idn = id;

    }

    public String getEp_num() {
        return ep_num;
    }

    public String getTitle_nm() {
        return title_nm;
    }

    public String getUrl_imagetitle() {
        return url_imagetitle;
    }

    public String getIdn() {
        return idn;
    }
}
