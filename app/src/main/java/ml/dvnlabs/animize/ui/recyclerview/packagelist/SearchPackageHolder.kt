/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.recyclerview.packagelist

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.model.SearchListPackageModel
import ml.dvnlabs.animize.ui.activity.PackageView

class SearchPackageHolder(private val context: Context, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private var model: SearchListPackageModel? = null
    private val container: CardView
    private val titlename: TextView
    private val rate: TextView
    private val episode: TextView
    private val cover: ImageView
    fun bind_search_package(model: SearchListPackageModel) {
        this.model = model
        titlename.text = model.title
        rate.text = model.rating
        val ep = model.now + " OF " + model.tot
        episode.text = ep
        Glide.with(context)
                .applyDefaultRequestOptions(RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(model.cover).transform(RoundedCornersTransformation(10, 0))
                .transition(DrawableTransitionOptions()
                        .crossFade()).apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600)).into(cover)
        Glide.with(context)
                .applyDefaultRequestOptions(RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(model.cover).transform(BlurTransformation(25, 3))
                .transition(DrawableTransitionOptions()
                        .crossFade()).apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600)).into(object : CustomTarget<Drawable?>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                        container.background = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        container.background = placeholder
                    }
                })
    }

    override fun onClick(v: View) {
        if (model != null) {
            val intent = Intent(context, PackageView::class.java)
            intent.putExtra("package", model!!.pkgid)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    init {
        container = view.findViewById(R.id.searchpack_container)
        cover = view.findViewById(R.id.searchpackage_cover)
        titlename = view.findViewById(R.id.searchpackage_name)
        rate = view.findViewById(R.id.searchpackages_rate)
        episode = view.findViewById(R.id.searchpackage_episode)
        container.setOnClickListener(this)
    }
}