package ml.dvnlabs.animize.fragment;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.dashboard_activity;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class global {
    public static void addFragment(FragmentManager fragmentManager, Fragment fragment, int id, String name, String anim_res){
        final int newBackStackLength = fragmentManager.getBackStackEntryCount() +1;
        int anim_enter = 0;
        int anim_exit = 0;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(anim_res.equals("ZOOM")){
            anim_enter = R.anim.zoom_in;
            anim_exit = R.anim.zoom_out;
        }
        if(anim_res.equals("SLIDE")){
            anim_exit = R.anim.slide_up;
            anim_enter = R.anim.slide_down;
        }
        fragmentTransaction.setCustomAnimations(anim_enter,anim_exit);
        fragmentTransaction.replace(id,fragment);
        final int count = fragmentManager.getBackStackEntryCount();
        Log.e("COUNTED+:",String.valueOf(count));
        if(name.equals("FRAGMENT_OTHER")){
            fragmentTransaction.addToBackStack(name);
        }
        fragmentTransaction.commit();
        //fragmentManager.beginTransaction().replace(id, fragment).addToBackStack(name).commit();
    }
}
