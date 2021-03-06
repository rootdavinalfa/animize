/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.base

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    /**
     * Change status bar color by using following parameter
     *
     * [activity] Fill with requester Activity eg:MainActivity or using 'this'
     *
     * [color] Fill with ID
     *
     * [lightThemeIcon] Statement using light icon or not (Default True)
     *
     * */
    protected open fun changeStatusBar(activity: Activity, color: Int, lightThemeIcon: Boolean = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = window.decorView.systemUiVisibility
            if (lightThemeIcon) {
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.decorView.systemUiVisibility = flags
            activity.window.statusBarColor = getColor(color)
        }
    }
}