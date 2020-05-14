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
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.model.SearchListModel
import ml.dvnlabs.animize.ui.activity.StreamActivity

class SearchListHolder(private val context: Context, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private val title_nm: TextView
    private val ep_num: TextView
    private val idn: TextView
    private val title_image: ImageView
    private var vl_model: SearchListModel? = null
    fun bindsearch_list(vlm: SearchListModel?) {
        vl_model = vlm
        //  Log.e("DATAAA:",vl_model.getTitle_nm());
        title_nm.text = vl_model!!.title_nm
        //Log.e("INFOEW",vl_model.getTitle_nm());
        Glide.with(itemView).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.ic_picture).error(R.drawable.ic_picture)).load(vl_model!!.urlImageTitle).transition(DrawableTransitionOptions().crossFade()).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).override(600, 200).fitCenter()).into(title_image)
        idn.text = vl_model!!.idAnim
        val ep = context.getString(R.string.list_view_episode) + vl_model!!.ep_num
        ep_num.text = ep
    }

    override fun onClick(v: View) {
        if (vl_model != null) {
            val intent = Intent(context.applicationContext, StreamActivity::class.java)
            intent.putExtra("id_anim", vl_model!!.idAnim)
            context.startActivity(intent)
        }
    }

    init {
        title_nm = view.findViewById<View>(R.id.srctitle_video_list) as TextView
        ep_num = view.findViewById<View>(R.id.srcepisode_view) as TextView
        idn = view.findViewById<View>(R.id.srcid_anime) as TextView
        title_image = view.findViewById<View>(R.id.srctitle_image) as ImageView
        itemView.setOnClickListener(this)
    }
}