/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.recyclerview.list

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.card.MaterialCardView
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.model.HomeLastUploadModel
import ml.dvnlabs.animize.ui.activity.StreamActivity

class HomeLastUpHolder(private val context: Context, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
    var title: TextView
    var episode: TextView

    //ImageView img_src;
    var container: MaterialCardView
    private var model: HomeLastUploadModel? = null
    fun bindLastUp(plm: HomeLastUploadModel?) {
        model = plm
        title.text = model!!.title_nm
        val ep = context.getString(R.string.episode_text) + ": " + model!!.ep_num
        episode.text = ep
        Glide.with(context)
                .applyDefaultRequestOptions(RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(model!!.urlImageTitle).transform(RoundedCornersTransformation(5, 0))
                .transition(DrawableTransitionOptions()
                        .crossFade()).apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)).into(object : CustomTarget<Drawable?>() {
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
            val intent = Intent(context.applicationContext, StreamActivity::class.java)
            intent.putExtra("id_anim", model!!.idn)
            context.startActivity(intent)
        }
    }

    init {
        episode = view.findViewById(R.id.episode_lastupload_home)
        title = view.findViewById(R.id.title_lastupload_home)
        container = view.findViewById(R.id.newepisode_container)
        itemView.setOnClickListener(this)
    }
}