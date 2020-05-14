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
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.model.PackageList
import ml.dvnlabs.animize.ui.activity.PackageView
import java.util.*

class LastPackageHolder(private val mContext: Context, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private var genres: List<String> = ArrayList()
    private val rate: TextView
    private val mal: TextView
    private val ep: TextView
    private val name: TextView
    private val genre: TextView
    private val cover: ImageView
    private var data: PackageList? = null
    private val container: CardView
    fun binding(data: PackageList) {
        this.data = data
        rate.text = data.rate
        val ep_string = data.now + " " + mContext.getString(R.string.string_of) + " " + data.tot
        ep.text = ep_string
        genres = data.genre
        val sb = StringBuilder()
        for (counter in genres.indices) {
            sb.append(genres[counter])
            if (genres.size - 1 > counter) {
                sb.append(",")
            }
        }
        val genree = sb.toString()
        name.text = data.getName()
        genre.text = genree
        val mals = "MAL: " + data.mal
        mal.text = mals
        Glide.with(mContext)
                .applyDefaultRequestOptions(RequestOptions()
                        .placeholder(R.drawable.ic_picture_light)
                        .error(R.drawable.ic_picture_light))
                .load(data.coverUrl).transform(RoundedCornersTransformation(10, 0))
                .transition(DrawableTransitionOptions()
                        .crossFade()).apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600)).into(cover)
        val multi = MultiTransformation(
                BlurTransformation(40), RoundedCornersTransformation(5, 0)
        )
        Glide.with(mContext)
                .applyDefaultRequestOptions(RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(data.coverUrl).transform(multi)
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
        if (data != null) {
            val intent = Intent(mContext, PackageView::class.java)
            intent.putExtra("package", data!!.pack)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mContext.startActivity(intent)
            Log.e("CLICK:", data!!.pack)
        }
    }

    init {
        rate = view.findViewById(R.id.package_rate)
        ep = view.findViewById(R.id.package_episode)
        name = view.findViewById(R.id.package_name)
        genre = view.findViewById(R.id.package_genre)
        mal = view.findViewById(R.id.package_mal)
        cover = view.findViewById(R.id.package_cover)
        container = view.findViewById(R.id.rv_lastpackage_container)
        itemView.setOnClickListener(this)
    }
}