/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.recyclerview;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.wang.avi.AVLoadingIndicatorView;

import ml.dvnlabs.animize.R;

public class progress_holder  extends RecyclerView.ViewHolder {
    public AVLoadingIndicatorView loading;

    public progress_holder(Context context,View view) {
        super(view);
        loading = (AVLoadingIndicatorView) view.findViewById(R.id.load_moreprogress);
    }
}
