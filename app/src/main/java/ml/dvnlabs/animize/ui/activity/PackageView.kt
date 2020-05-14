/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import com.wang.avi.AVLoadingIndicatorView
import io.branch.referral.Branch
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.database.InitInternalDBHelper
import ml.dvnlabs.animize.database.PackageStarDBHelper
import ml.dvnlabs.animize.database.RecentPlayDBHelper
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.APINetworkRequest
import ml.dvnlabs.animize.driver.util.listener.FetchDataListener
import ml.dvnlabs.animize.model.PackageInfo
import ml.dvnlabs.animize.model.PlaylistModel
import ml.dvnlabs.animize.ui.activity.MainActivity.Companion.setWindowFlag
import ml.dvnlabs.animize.ui.fragment.information.CoverView
import ml.dvnlabs.animize.ui.recyclerview.packagelist.PackageListAdapter
import ml.dvnlabs.animize.ui.recyclerview.staggered.package_metagenre_adapter
import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

class PackageView : AppCompatActivity() {
    private var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var appBarLayout: AppBarLayout? = null
    private var notFoundContainer: RelativeLayout? = null
    private var loadingcontainer: RelativeLayout? = null
    private var coverToolbar: ImageView? = null
    private var recentImg: ImageView? = null
    private var playlistModels: ArrayList<PlaylistModel>? = null
    private var modelinfo: ArrayList<PackageInfo>? = null
    var adapter: PackageListAdapter? = null
    private var listview: RecyclerView? = null
    private var genrelist: RecyclerView? = null
    private var synops: TextView? = null
    private var recentTitle: TextView? = null
    private var recentEpisode: TextView? = null
    private var rate: TextView? = null
    private var animeid: TextView? = null
    private var packageParent: CoordinatorLayout? = null
    private var recentContainer: RelativeLayout? = null
    var pkganim: String? = null
    private var packStar: MenuItem? = null
    private var packShare: MenuItem? = null
    var packageStarDBHelper: PackageStarDBHelper? = null
    var recentPlayDBHelper: RecentPlayDBHelper? = null
    private var container: NestedScrollView? = null
    private var loading: AVLoadingIndicatorView? = null
    private var branch: Branch? = null
    var initInternalDBHelper: InitInternalDBHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initInternalDBHelper = InitInternalDBHelper(this)
        if (initInternalDBHelper!!.userCount) {
            setContentView(R.layout.activity_package_view)
            //make translucent statusBar on kitkat devices
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            //make fully Android Transparent Status bar
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
            pkganim = ""
            initialize()
            val intent = intent
            if (getIntent().getStringExtra("package") != null) {
                //setIdanim(intent.getStringExtra("id_anim"));
                pkganim = intent.getStringExtra("package")
                //intent.removeExtra("id_anim");
            }
            assert(pkganim != null)
            if (pkganim!!.isNotEmpty()) {

                //System.out.println(pkganim);
                //initializeADSandDB();
                getInfo()
            }
            initializeADSandDB()
            branch = Branch.getInstance()
        } else {
            val intent = Intent(this@PackageView, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeADSandDB() {
        packageStarDBHelper = PackageStarDBHelper(this)
        recentPlayDBHelper = RecentPlayDBHelper(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initialize() {
        val toolbar = findViewById<Toolbar>(R.id.pv_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.title = ""
        collapsingToolbarLayout = findViewById(R.id.pv_collapse_toolbar)
        collapsingToolbarLayout!!.title = "Loading..."
        listview = findViewById(R.id.packageview_list)
        genrelist = findViewById(R.id.packageview_genrelist)
        synops = findViewById(R.id.packageview_synopsis)
        container = findViewById(R.id.packageview_container)
        loading = findViewById(R.id.packageview_loading)
        coverToolbar = findViewById(R.id.toolbar_cover)
        packageParent = findViewById(R.id.package_parent)
        recentContainer = findViewById(R.id.packageview_recent_container)
        recentEpisode = findViewById(R.id.packageview_recent_episode)
        recentTitle = findViewById(R.id.packageview_recent_title)
        recentImg = findViewById(R.id.packageview_recent_img)
        rate = findViewById(R.id.package_rate)
        animeid = findViewById(R.id.package_animeid)
        appBarLayout = findViewById(R.id.packageview_barlayout)
        notFoundContainer = findViewById(R.id.packageview_notfoundcont)
        loadingcontainer = findViewById(R.id.packageview_loading_container)
        modelinfo = ArrayList()
        collapsingToolbarLayout!!.maxLines = 3
        loadingcontainer!!.visibility = View.VISIBLE
        container!!.visibility = View.GONE
        appBarLayout!!.visibility = View.GONE
    }

    /*Function for refreshing activity data due to usage of multiview fragment*/
    fun refreshActivity(pkgid: String?) {
        pkganim = pkgid
        if (!pkganim!!.isEmpty()) {
            if (modelinfo!!.size > 0) {
                modelinfo!!.clear()
            }
            loadingcontainer!!.visibility = View.VISIBLE
            container!!.visibility = View.GONE
            appBarLayout!!.visibility = View.GONE
            getInfo()
        }
        initializeADSandDB()
    }

    override fun onBackPressed() {
        if (isTaskRoot) {
            finish()
            val intent = Intent(this@PackageView, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (initInternalDBHelper!!.userCount) {
            // Branch init
            if (branch != null) {
                branch!!.initSession({ referringParams, error ->
                    if (error == null) {
                        // params are the deep linked params associated with the link      that the user clicked -> was re-directed to this app
                        // params will be empty if no data found
                        try {
                            if (referringParams.getBoolean("+clicked_branch_link")) {
                                pkganim = referringParams.getString("pack")
                                getInfo()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.e("BRANCH SDKE", error.message)
                    }
                }, this.intent.data, this)
            }
            if (pkganim!!.isNotEmpty()) {
                showRecent()
                invalidateOptionsMenu()
            }
        } else {
            val intent = Intent(this@PackageView, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.package_toolbar, menu)
        packStar = menu.findItem(R.id.package_star)
        packShare = menu.findItem(R.id.package_share)
        if (packageStarDBHelper!!.isAvail) {
            GlobalScope.launch {
                readStarStatus()
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.package_star -> {
                if (packageStarDBHelper!!.isStarred(pkganim!!)) {
                    GlobalScope.launch {
                        changeStar("UNSTAR")
                    }
                } else {
                    GlobalScope.launch {
                        changeStar("STAR")
                    }
                }
                return true
            }
            R.id.package_share -> if (modelinfo != null) {
                // Add data to the intent, the receiving app will decide
                // what to do with it.
                var urlShare: String? = null
                try {
                    urlShare = "https://animize.app.link/share/package?pack=" + pkganim + "&titname=" + URLEncoder.encode(modelinfo!![0].getName(), "utf-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra(Intent.EXTRA_TEXT, urlShare)
                //Here we're setting the title of the content
                sendIntent.putExtra(Intent.EXTRA_TITLE, "Share " + modelinfo!![0].getName() + " to your friend!")
                sendIntent.type = "text/plain"
                // Show the ShareSheet
                startActivity(Intent.createChooser(sendIntent, null))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getPlayList() {
        val url = Api.url_playlist_play + pkganim
        APINetworkRequest(this, getplaylist, url, CODE_GET_REQUEST, null)
    }

    private fun getInfo() {
        val url = Api.url_packageinfo + pkganim
        APINetworkRequest(this, getInfo, url, CODE_GET_REQUEST, null)
    }

    var getInfo: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    parserInfo(`object`.getJSONArray("anim"))
                } else {
                    window.statusBarColor = Color.RED
                    appBarLayout!!.visibility = View.GONE
                    container!!.visibility = View.GONE
                    loadingcontainer!!.visibility = View.GONE
                    loading!!.visibility = View.GONE
                    notFoundContainer!!.visibility = View.VISIBLE
                    packageParent!!.background = null
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {}
        override fun onFetchStart() {
            loading!!.visibility = View.VISIBLE
            container!!.visibility = View.GONE
            appBarLayout!!.visibility = View.GONE
            notFoundContainer!!.visibility = View.GONE
        }
    }
    var getplaylist: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    loadingcontainer!!.visibility = View.GONE
                    loading!!.visibility = View.GONE
                    notFoundContainer!!.visibility = View.GONE
                    appBarLayout!!.visibility = View.VISIBLE
                    container!!.visibility = View.VISIBLE
                    parsePlayList(`object`.getJSONArray("anim"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {}
        override fun onFetchStart() {}
    }

    private fun parserInfo(info: JSONArray) {
        try {
            modelinfo!!.clear()
            for (i in 0 until info.length()) {
                val `object` = info.getJSONObject(i)
                val packages = `object`.getString("package_anim")
                val nameAnim = `object`.getString("name_catalogue")
                val synopsis = `object`.getString("synopsis")
                val totalEP = `object`.getString("total_ep_anim")
                val rate = `object`.getString("rating")
                val mal = `object`.getString("mal_id")
                val cover = `object`.getString("cover")
                val genreJson = `object`.getJSONArray("genre")
                val genres: MutableList<String> = ArrayList()
                for (j in 0 until genreJson.length()) {
                    genres.add(genreJson.getString(j))
                    //Log.e("GENRES:",genre_json.getString(j));
                }
                modelinfo!!.add(PackageInfo(packages, nameAnim, synopsis, totalEP, rate, mal, genres, cover))
            }
            collapsingToolbarLayout!!.title = modelinfo!![0].getName()
            synops!!.text = modelinfo!![0].synopsis
            val adapterGenre = package_metagenre_adapter(modelinfo!![0].genre, this, R.layout.rv_staggered)
            val spanStaggered = if (adapterGenre.itemCount < 7) 1 else 2
            val layoutManager = StaggeredGridLayoutManager(spanStaggered, StaggeredGridLayoutManager.HORIZONTAL)
            genrelist!!.layoutManager = layoutManager
            genrelist!!.adapter = adapterGenre
            Glide.with(this)
                    .applyDefaultRequestOptions(RequestOptions()
                            .placeholder(R.drawable.ic_picture)
                            .error(R.drawable.ic_picture))
                    .load(modelinfo!![0].cover)
                    .transition(DrawableTransitionOptions()
                            .crossFade()).apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600)).into(coverToolbar!!)
            Glide.with(this)
                    .applyDefaultRequestOptions(RequestOptions()
                            .placeholder(R.drawable.ic_picture)
                            .error(R.drawable.ic_picture))
                    .load(modelinfo!![0].cover).transform(BlurTransformation(10, 3))
                    .transition(DrawableTransitionOptions()
                            .crossFade()).apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600)).into(object : CustomTarget<Drawable?>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                            packageParent!!.background = resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            packageParent!!.background = placeholder
                        }
                    })
            coverToolbar!!.setOnClickListener {
                val fm = supportFragmentManager
                val alertDialog = CoverView()
                alertDialog.setUrl(modelinfo!![0].cover)
                alertDialog.show(fm, "coverview")
            }
            rate!!.text = modelinfo!![0].rate
            val animeID = modelinfo!![0].pack + " / " + modelinfo!![0].mal
            animeid!!.text = animeID
            showRecent()
            invalidateOptionsMenu()
            getPlayList()
        } catch (e: JSONException) {
            Log.e("JSON ERROR:", e.toString())
        }
    }

    private fun showRecent() {
        if (recentPlayDBHelper!!.isRecentPackAvail(pkganim!!)) {
            GlobalScope.launch { readRecent() }
        } else {
            recentContainer!!.visibility = View.GONE
        }
    }

    private fun parsePlayList(playlist: JSONArray) {
        try {
            playlistModels = ArrayList()
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            listview!!.layoutManager = layoutManager
            for (i in 0 until playlist.length()) {
                val `object` = playlist.getJSONObject(i)
                val urlImg = `object`.getString("thumbnail")
                val title = `object`.getString("name_catalogue")
                val episode = `object`.getString("episode_anim")
                val idAnim = `object`.getString("id_anim")
                val pkg = `object`.getString("package_anim")
                playlistModels!!.add(PlaylistModel(urlImg, title, episode, idAnim, pkg))
            }
            adapter = PackageListAdapter(playlistModels, this, R.layout.package_playlist_view)
            listview!!.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun readStarStatus(){
        withContext(Dispatchers.IO){
            val isStarred = packageStarDBHelper!!.isStarred(pkganim!!)
            withContext(Dispatchers.Main){
                if (isStarred) {
                    packStar!!.setIcon(R.drawable.ic_star)
                } else {
                    packStar!!.setIcon(R.drawable.ic_star_nofill)
                }
            }
        }
    }

    private suspend fun changeStar(change : String){
        withContext(Dispatchers.IO){
            if (change == "UNSTAR") {
                packageStarDBHelper!!.unStar(pkganim!!)
            } else if (change == "STAR") {
                packageStarDBHelper!!.addStar(pkganim!!)
            }
            val isStarred = packageStarDBHelper!!.isStarred(pkganim!!)
            withContext(Dispatchers.Main){
                val status: String = if (isStarred) {
                    packStar!!.setIcon(R.drawable.ic_star)
                    "Add to Star Success"
                } else {
                    packStar!!.setIcon(R.drawable.ic_star_nofill)
                    "Remove Star Success"
                }
                Toast.makeText(applicationContext, status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun readRecent(){
        withContext(Dispatchers.IO){
            val recentLand = recentPlayDBHelper!!.readRecentOnPackage(pkganim!!)
            withContext(Dispatchers.Main){
                recentContainer!!.visibility = View.VISIBLE
                recentTitle!!.text = recentLand!!.packageName
                val ep = "Episode: " + recentLand.episode
                recentEpisode!!.text = ep
                Glide.with(baseContext)
                        .applyDefaultRequestOptions(RequestOptions()
                                .placeholder(R.drawable.ic_picture)
                                .error(R.drawable.ic_picture))
                        .load(recentLand.urlCover).transform(RoundedCornersTransformation(10, 0))
                        .transition(DrawableTransitionOptions()
                                .crossFade()).apply(RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600)).into(object : CustomTarget<Drawable?>() {
                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                                recentImg!!.background = resource
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                recentImg!!.background = placeholder
                            }
                        })
                recentContainer!!.setOnClickListener {
                    val intent = Intent(applicationContext, StreamActivity::class.java)
                    intent.putExtra("id_anim", recentLand.anmid)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    applicationContext.startActivity(intent)
                }
            }
        }
    }

    companion object {
        private const val CODE_GET_REQUEST = 1024
    }
}