package ml.dvnlabs.animize.fragment.tabs.animplay

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.activity.animplay_activity
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.APINetworkRequest
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener
import ml.dvnlabs.animize.model.playlist_model
import ml.dvnlabs.animize.recyclerview.list.playlist_adapter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class more : Fragment(){
    var mcontext : Context? = null
    val CODE_GET_REQUEST : Int = 1024
    private var sourceid: ImageView? = null;var sourceen:ImageView? = null;var sourceraw:ImageView? = null
    private var playlist_models: ArrayList<playlist_model>? = null
    var adapter: playlist_adapter?= null
    private var listview: RecyclerView? = null
    var pkganim: String? = null;var id_anim:String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mcontext = context
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.ap_fragment_more_tabs, container, false)
        listview = view.findViewById(R.id.playlist_list)
        sourceen = view.findViewById(R.id.animplay_btn_sourceEN)
        sourceid = view.findViewById(R.id.animplay_btn_sourceID)
        sourceraw = view.findViewById(R.id.animplay_btn_sourceRAW)
        initial()
        return  view
    }
    private fun  initial(){
        sourceen!!.setOnClickListener(View.OnClickListener { sourceselector("EN") })
        sourceid!!.setOnClickListener(View.OnClickListener { sourceselector("ID") })
        sourceraw!!.setOnClickListener(View.OnClickListener { sourceselector("RAW") })
    }
    private fun sourceselector(lang: String) {
        (context as animplay_activity).showsourceselector(lang, id_anim)
    }
    private fun getplaylist(){
        var url : String = Api.url_playlist_play + pkganim
        APINetworkRequest(mcontext,getPlaylist,url,CODE_GET_REQUEST,null)
    }
    var getPlaylist : FetchDataListener = object : FetchDataListener{
        override fun onFetchComplete(data: String?) {
            try {
                val `object` = JSONObject(data)
                if (!`object`.getBoolean("error")) {
                    parseplaylist(`object`.getJSONArray("anim"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        override fun onFetchFailure(msg: String?) {

        }

        override fun onFetchStart() {

        }
    }
    private fun parseplaylist(playlist : JSONArray){
        try {
            playlist_models = ArrayList()
            val layoutManager = LinearLayoutManager(activity)
            listview!!.setLayoutManager(layoutManager)

            for (i in 0 until playlist.length()) {
                val `object` = playlist.getJSONObject(i)
                val url_img = `object`.getString("thumbnail")
                val title = `object`.getString("name_catalogue")
                val episode = `object`.getString("episode_anim")
                val id_an = `object`.getString("id_anim")
                val pkg = `object`.getString("package_anim")
                playlist_models!!.add(playlist_model(url_img, title, episode, id_an, pkg))

            }
            adapter = playlist_adapter(playlist_models, activity, R.layout.playlist_view, id_anim)
            listview!!.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun receivedata(pkg: String, anim: String) {
        pkganim = pkg
        id_anim = anim
        getplaylist()
    }
}