package ml.dvnlabs.animize.recyclerview.interfaces;

import ml.dvnlabs.animize.recyclerview.packagelist.starlist_adapter;

import java.util.ArrayList;

public interface addingQueue {
    void addQueue(String pkganim,int position);
    void removeQueue(int position);
    ArrayList<starlist_adapter.requestQueue> getQueue();
}
