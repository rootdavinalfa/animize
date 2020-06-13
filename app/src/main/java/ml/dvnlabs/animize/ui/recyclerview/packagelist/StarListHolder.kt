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
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.database.legacy.model.StarLand
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.network.APINetworkRequest
import ml.dvnlabs.animize.driver.network.listener.FetchDataListener
import ml.dvnlabs.animize.model.StarredModel
import ml.dvnlabs.animize.ui.activity.PackageView
import ml.dvnlabs.animize.ui.recyclerview.interfaces.AddingQueue
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class StarListHolder(context : Context, view: View, listener : AddingQueue, private val sizePackageList : Int): RecyclerView.ViewHolder(view), View.OnClickListener {
    private val mContext = context
    private val episode: TextView = view.findViewById(R.id.star_episode)
    private val rate: TextView = view.findViewById(R.id.star_rate)
    private var mal:TextView = view.findViewById(R.id.star_mal)
    private val title: TextView = view.findViewById(R.id.star_name)
    private val thumbnail: ImageView = view.findViewById(R.id.star_cover)

    private var data: StarLand? = null
    private val loading: ShimmerFrameLayout = view.findViewById(R.id.rv_shimmer_recent)
    private val recent_layout: LinearLayout = view.findViewById(R.id.rv_item_recent)
    private var pkgid : String? = null
    private var listeners = listener
    //private lateinit var listener : addingQueue

    private var starmodel : StarredModel? = null
    private var ready  = StarListAdapter.readyStars

    init {
        itemView.setOnClickListener(this)
    }

    fun bindPlaylist(model : StarLand){
        println(ready.size)
        data = model
        pkgid = data!!.packageid
        //println(ready.size)
        if (ready.size != 0){
            for (i in ready){
                if (i.pos == absoluteAdapterPosition) {
                    //println("USE READY")
                    starmodel = i.model
                    copyData()
                }
            }
            if (ready.size < sizePackageList){
                makeRequest(pkgid!!)
            }
        }else{
            makeRequest(pkgid!!)
        }

    }

    private fun makeRequest(pkg : String){
        val url : String = Api.url_packageinfo+pkg
        APINetworkRequest(mContext, fetchPackage, url, 1024, null)
        listeners.addQueue(pkgid!!, absoluteAdapterPosition)
    }

    private val fetchPackage : FetchDataListener = object : FetchDataListener{
        override fun onFetchComplete(data: String?) {
            loading.stopShimmer()
            loading.visibility= View.GONE
            recent_layout.visibility = View.VISIBLE
            try {
                val objects = JSONObject(data!!)
                if (!objects.getBoolean("error")){
                    listeners.removeQueue(absoluteAdapterPosition)

                    addToArrayPackage(objects.getJSONArray("anim"))
                }
            }catch (e : JSONException){
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {
            val queue = listeners.queue
            for (i in 0 until queue!!.size){
                if (queue[i]!!.pos == absoluteAdapterPosition) {
                    makeRequest(queue[i]!!.pkg)
                }
            }
        }

        override fun onFetchStart() {
            loading.startShimmer()
            loading.visibility = View.VISIBLE
            recent_layout.visibility = View.GONE
        }
    }

    private fun addToArrayPackage(pack: JSONArray) {
        try {
            for (i in 0 until pack.length()) {
                val `object`: JSONObject = pack.getJSONObject(i)
                val packages: String = `object`.getString("package_anim")
                val nameanim: String? = `object`.getString("name_catalogue")
                val totep: String? = `object`.getString("total_ep_anim")
                val rate: String? = `object`.getString("rating")
                val mal: String? = `object`.getString("mal_id")
                val cover: String? = `object`.getString("cover")
                starmodel = StarredModel(packages, nameanim!!, totep!!, rate!!, mal!!, cover!!)
            }
            ready.add(StarListAdapter.readyStar(starmodel!!, absoluteAdapterPosition))
            StarListAdapter.readyStars = ready
            copyData()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun copyData() {
        for (i in ready) {
            if (i.pos == absoluteAdapterPosition) {
                val model = i.model
                title.text = model.name
                val ep_string: String = mContext.getString(R.string.list_view_episode) + model.total_ep
                episode.text = ep_string
                rate.text = model.rating
                val mals = "MAL: " + model.mal
                mal.text = mals
                Glide.with(mContext)
                        .applyDefaultRequestOptions(RequestOptions()
                                .placeholder(R.drawable.ic_picture)
                                .error(R.drawable.ic_picture))
                        .load(model.cover)
                        .transition(DrawableTransitionOptions()
                                .crossFade()).apply(RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600)).into(thumbnail)
            }
        }
    }


    override fun onClick(v: View?) {
        if (data != null){
            val intent = Intent(mContext, PackageView::class.java)
            intent.putExtra("package", data!!.packageid)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mContext.startActivity(intent)
        }
    }



}