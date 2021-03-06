/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.fragment

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ml.dvnlabs.animize.R

class Global{
    companion object{
        fun addFragment(fragmentManager: FragmentManager,fragment: Fragment,id: Int ,name: String,anim_res: String){
            //val newBackStackLength = fragmentManager.backStackEntryCount + 1
            var anim_enter = 0
            var anim_exit = 0
            val fragmentTransaction = fragmentManager.beginTransaction()
            if (anim_res != "NULL") {
                if (anim_res == "ZOOM") {
                    anim_enter = R.anim.zoom_in
                    anim_exit = R.anim.zoom_out
                }
                if (anim_res == "SLIDE") {
                    anim_exit = R.anim.slide_down
                    anim_enter = R.anim.slide_up
                }
                fragmentTransaction.setCustomAnimations(anim_enter, anim_exit)
            }
            fragmentTransaction.replace(id, fragment)
            val count = fragmentManager.backStackEntryCount
            Log.e("COUNTED+:", count.toString())
            if (name == "FRAGMENT_OTHER") {
                fragmentTransaction.addToBackStack(name)
            }
            fragmentTransaction.commit()
        }
    }
}