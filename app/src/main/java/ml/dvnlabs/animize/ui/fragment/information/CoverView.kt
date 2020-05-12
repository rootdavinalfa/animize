/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.fragment.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import ml.dvnlabs.animize.R

class CoverView : DialogFragment() {
    private var img: ImageView? = null
    private var url: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_coverview, container, false)
        img = view.findViewById(R.id.coverview_img)
        return view
    }

    fun newInstance(): CoverView {
        return CoverView()
    }

    fun setUrl(url: String) {
        this.url = url
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Glide.with(this)
                .applyDefaultRequestOptions(RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(url)
                .transition(DrawableTransitionOptions()
                        .crossFade()).apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).override(600, 991)).into(img!!)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.bannerdialog)
    }

    override fun onResume() {
        setStyle(STYLE_NO_TITLE, R.style.bannerdialog)
        super.onResume()
    }
}