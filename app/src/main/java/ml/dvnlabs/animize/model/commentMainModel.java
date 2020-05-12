/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.model;

import java.util.ArrayList;

public class commentMainModel {
    private String idcomment,status,content;
    private ArrayList<api_usermodel> usermodels;

    public commentMainModel(String id, String status, String content, ArrayList<api_usermodel> models) {
        this.content = content;
        this.status = status;
        this.idcomment = id;
        this.usermodels = models;
    }

    public ArrayList<api_usermodel> getUsermodels() {
        return usermodels;
    }

    public String getContents() {
        return content;
    }

    public String getIdcomment() {
        return idcomment;
    }

    public String getStatus() {
        return status;
    }
}
