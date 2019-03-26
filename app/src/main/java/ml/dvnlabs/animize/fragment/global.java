package ml.dvnlabs.animize.fragment;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ml.dvnlabs.animize.R;
import ml.dvnlabs.animize.activity.animlist_activity;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class global {
    public static void addFragment(FragmentManager fragmentManager, Fragment fragment, int id,String name){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.zoom_in,R.anim.zoom_out);
        fragmentTransaction.replace(id,fragment);
        final int count = fragmentManager.getBackStackEntryCount();
        Log.e("COUNTED+:",String.valueOf(count));
        if(name.equals("FRAGMENT_OTHER")){
            fragmentTransaction.addToBackStack(name);
        }
        fragmentTransaction.commit();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                // If the stack decreases it means I clicked the back button
                if( fragmentManager.getBackStackEntryCount() <= count){
                    // pop all the fragment and remove the listener
                    fragmentManager.popBackStack("FRAGMENT_OTHER", POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.removeOnBackStackChangedListener(this);
                    // set the home button selected
                    video_list vl = video_list.newInstance();
                    global.addFragment(fragmentManager,vl, R.id.video_list_fragment,"FRAGMENT_HOME");
                }
            }
        });



        //fragmentManager.beginTransaction().replace(id, fragment).addToBackStack(name).commit();
    }
}
