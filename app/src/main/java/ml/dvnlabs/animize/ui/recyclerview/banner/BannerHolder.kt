/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.recyclerview.banner

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.rv_banner.view.*
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.model.BannerListMdl
import ml.dvnlabs.animize.ui.activity.WebView

class BannerHolder(private val context: Context, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private var model: BannerListMdl? = null
    fun bindBanner(BannerListMdl: BannerListMdl?) {
        model = BannerListMdl
        Glide.with(itemView).applyDefaultRequestOptions(RequestOptions()
                .placeholder(R.drawable.ic_picture)
                .error(R.drawable.ic_picture))
                .load(model!!.banner_image)
                .transition(DrawableTransitionOptions()
                        .crossFade()).apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)).fitCenter().into(itemView.banner_image)
        itemView.banner_title.text = model!!.banner_title
    }

    override fun onClick(v: View) {
        val intent = Intent(context.applicationContext, WebView::class.java)
        intent.putExtra("url", model!!.banner_url)
        context.startActivity(intent)
    }

    init {
        itemView.setOnClickListener(this)
    }
}