/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.Event;

public class PlayerBusStatus {
    private final String status;

    public PlayerBusStatus(String status1){
        this.status = status1;
    }

    public String getStatus(){
        return status;
    }
}
