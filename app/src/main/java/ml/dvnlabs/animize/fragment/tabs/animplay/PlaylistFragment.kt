package ml.dvnlabs.animize.fragment.tabs.animplay

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.APINetworkRequest
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener
import ml.dvnlabs.animize.model.playlist_model
import ml.dvnlabs.animize.recyclerview.list.playlist_adapter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class PlaylistFragment : Fragment(){
    var mcontext : Context? = null
    val CODE_GET_REQUEST : Int = 1024
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
        return  view
    }
    private fun getplaylist(){
        val url : String = Api.url_playlist_play + pkganim
        mcontext?.let { APINetworkRequest(it, getPlaylist, url, CODE_GET_REQUEST, null) }
    }
    private var getPlaylist : FetchDataListener = object : FetchDataListener{
        override fun onFetchComplete(data: String?) {
            try {
                val `object` = JSONObject(data)
                if (!`object`.getBoolean("error")) {
                    parsePlaylist(`object`.getJSONArray("anim"))
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
    private fun parsePlaylist(playlist : JSONArray){
        try {
            playlist_models = ArrayList()
            val layoutManager = LinearLayoutManager(activity)
            listview!!.layoutManager = layoutManager

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