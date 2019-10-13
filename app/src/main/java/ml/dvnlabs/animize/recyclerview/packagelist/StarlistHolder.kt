package ml.dvnlabs.animize.recyclerview.packagelist

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
import ml.dvnlabs.animize.activity.packageView
import ml.dvnlabs.animize.database.model.starland
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.APINetworkRequest
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener
import ml.dvnlabs.animize.model.starmodel
import ml.dvnlabs.animize.recyclerview.interfaces.addingQueue
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class StarlistHolder(context : Context, view: View,listener : addingQueue): RecyclerView.ViewHolder(view), View.OnClickListener {
    private val mContext = context
    private val episode: TextView = view.findViewById(R.id.star_episode)
    private val rate: TextView = view.findViewById(R.id.star_rate)
    private var mal:TextView = view.findViewById(R.id.star_mal)
    private val title: TextView = view.findViewById(R.id.star_name)
    private val thumbnail: ImageView = view.findViewById(R.id.star_cover)

    private var data: starland? = null
    private val loading: ShimmerFrameLayout = view.findViewById(R.id.rv_shimmer_recent)
    private val recent_layout: LinearLayout = view.findViewById(R.id.rv_item_recent)
    private var pkgid : String? = null
    private var listeners = listener
    //private lateinit var listener : addingQueue

    private var starmodel : starmodel? = null
    private var ready  = starlist_adapter.readyStars

    init {
        itemView.setOnClickListener(this)
    }

    fun bindPlaylist(model : starland){
        println(ready.size)
        data = model
        pkgid = data!!.packageid
        //println(ready.size)
        if (ready.size != 0){
            for (i in ready){
                if (i.pos == adapterPosition && i.model!= null){
                    //println("USE READY")
                    starmodel = i.model
                    copyData()
                }
            }
            if (ready.size < starlist_adapter.packagelists.size){
                makeRequest(pkgid!!)
            }
        }else{
            makeRequest(pkgid!!)
        }

    }

    private fun makeRequest(pkg : String){
        val url : String = Api.url_packageinfo+pkg
        APINetworkRequest(mContext,fetchPackage,url,1024,null)
        listeners.addQueue(pkgid,adapterPosition)
    }

    private val fetchPackage : FetchDataListener = object : FetchDataListener{
        override fun onFetchComplete(data: String?) {
            loading.stopShimmer()
            loading.visibility= View.GONE
            recent_layout.visibility = View.VISIBLE
            try {
                val objects = JSONObject(data);
                if (!objects.getBoolean("error")){
                    listeners.removeQueue(adapterPosition)

                    addToArrayPackage(objects.getJSONArray("anim"))
                }
            }catch (e : JSONException){
                e.printStackTrace();
            }
        }

        override fun onFetchFailure(msg: String?) {
            val queue = listeners.queue
            for (i in 0 until queue.size){
                if(queue[i].pos == adapterPosition){
                    makeRequest(queue[i].pkg)
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
                starmodel = starmodel(packages,nameanim,totep,rate,mal,cover)
            }
            ready.add(starlist_adapter.readyStar(starmodel,adapterPosition))
            starlist_adapter.readyStars = ready
            copyData()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun copyData() {
        for (i in ready) {
            if (i.pos == adapterPosition) {
                val model = i.model
                title.text = model.name
                val ep_string: String = mContext.getString(R.string.list_view_episode).toString() + model!!.total_ep
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
            val intent = Intent(mContext, packageView::class.java)
            intent.putExtra("package", data!!.packageid)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mContext.startActivity(intent)
            ////Log.e("CLICK:",this.data.getPackageid());
        }
    }



}