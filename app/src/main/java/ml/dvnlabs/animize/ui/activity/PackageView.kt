/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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
import ml.dvnlabs.animize.databinding.ActivityPackageViewBinding
import ml.dvnlabs.animize.driver.Api
import ml.dvnlabs.animize.driver.util.network.APINetworkRequest
import ml.dvnlabs.animize.driver.util.network.RequestQueueVolley
import ml.dvnlabs.animize.driver.util.network.listener.FetchDataListener
import ml.dvnlabs.animize.model.PackageInfo
import ml.dvnlabs.animize.model.PlaylistModel
import ml.dvnlabs.animize.ui.activity.MainActivity.Companion.setWindowFlag
import ml.dvnlabs.animize.ui.fragment.information.CoverView
import ml.dvnlabs.animize.ui.recyclerview.packagelist.PackageListAdapter
import ml.dvnlabs.animize.ui.recyclerview.staggered.PackageMetaGenreAdapter
import ml.dvnlabs.animize.view.AutoGridLayoutManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

class PackageView : AppCompatActivity() {
    private var playlistModels: ArrayList<PlaylistModel>? = null
    private var modelinfo: ArrayList<PackageInfo>? = null
    var adapter: PackageListAdapter? = null
    var pkganim: String? = null
    private var packStar: MenuItem? = null
    private var packShare: MenuItem? = null
    var packageStarDBHelper: PackageStarDBHelper? = null
    var recentPlayDBHelper: RecentPlayDBHelper? = null
    private var branch: Branch? = null
    var initInternalDBHelper: InitInternalDBHelper? = null

    private lateinit var binding: ActivityPackageViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initInternalDBHelper = InitInternalDBHelper(this)
        if (initInternalDBHelper!!.userCount) {
            binding = ActivityPackageViewBinding.inflate(layoutInflater)
            setContentView(binding.root)
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
            //assert(pkganim != null)
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

    override fun onPause() {
        RequestQueueVolley.getInstance(this)!!.cancelRequestByTAG("PLAYLIST")
        RequestQueueVolley.getInstance(this)!!.cancelRequestByTAG("INFO_PACKAGE")
        super.onPause()
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

    @SuppressLint("RestrictedApi")
    private fun initialize() {
        setSupportActionBar(binding.pvToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        binding.pvToolbar.title = ""
        binding.pvCollapseToolbar.title = "Loading..."

        modelinfo = ArrayList()
        binding.pvCollapseToolbar.maxLines = 3
        binding.packageviewLoadingContainer.visibility = View.VISIBLE
        binding.packageviewContainer.visibility = View.GONE

        binding.packageviewBarlayout.visibility = View.GONE

    }

    /*Function for refreshing activity data due to usage of multiview fragment*/
    fun refreshActivity(pkgid: String?) {
        pkganim = pkgid
        if (!pkganim!!.isEmpty()) {
            if (modelinfo!!.size > 0) {
                modelinfo!!.clear()
            }
            binding.packageviewLoadingContainer.visibility = View.VISIBLE
            binding.packageviewContainer.visibility = View.GONE
            binding.packageviewBarlayout.visibility = View.GONE
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
        when (item.itemId) {
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
        APINetworkRequest(this, getplaylist, url, CODE_GET_REQUEST, null,"PLAYLIST")
    }

    private fun getInfo() {
        val url = Api.url_packageinfo + pkganim
        APINetworkRequest(this, getInfo, url, CODE_GET_REQUEST, null,"INFO_PACKAGE")
    }

    private var getInfo: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    parserInfo(`object`.getJSONArray("anim"))
                } else {
                    window.statusBarColor = Color.RED
                    binding.packageviewBarlayout.visibility = View.GONE
                    binding.packageviewContainer.visibility = View.GONE
                    binding.packageviewLoadingContainer.visibility = View.GONE
                    binding.packageviewLoading.visibility = View.GONE
                    binding.packageviewNotfoundcont.visibility = View.VISIBLE
                    binding.packageParent.background = null
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String?) {}
        override fun onFetchStart() {
            binding.packageviewLoading.visibility = View.VISIBLE
            binding.packageviewContainer.visibility = View.GONE
            binding.packageviewBarlayout.visibility = View.GONE
            binding.packageviewNotfoundcont.visibility = View.GONE
        }
    }
    private var getplaylist: FetchDataListener = object : FetchDataListener {
        override fun onFetchComplete(data: String?) {
            try {
                val `object` = JSONObject(data!!)
                if (!`object`.getBoolean("error")) {
                    binding.packageviewLoadingContainer.visibility = View.GONE
                    binding.packageviewLoading.visibility = View.GONE
                    binding.packageviewNotfoundcont.visibility = View.GONE
                    binding.packageviewBarlayout.visibility = View.VISIBLE
                    binding.packageviewContainer.visibility = View.VISIBLE
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
            binding.pvCollapseToolbar.title = modelinfo!![0].getName()
            binding.packageviewSynopsis.text = modelinfo!![0].synopsis
            val adapterGenre = PackageMetaGenreAdapter(modelinfo!![0].genre, this, R.layout.rv_staggered)
            val spanStaggered = if (adapterGenre.itemCount < 7) 1 else 2
            val layoutManager = StaggeredGridLayoutManager(spanStaggered, StaggeredGridLayoutManager.HORIZONTAL)
            binding.packageviewGenrelist.layoutManager = layoutManager
            binding.packageviewGenrelist.adapter = adapterGenre
            Glide.with(this)
                    .applyDefaultRequestOptions(RequestOptions()
                            .placeholder(R.drawable.ic_picture)
                            .error(R.drawable.ic_picture))
                    .load(modelinfo!![0].cover)
                    .transition(DrawableTransitionOptions()
                            .crossFade()).apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600)).into(binding.toolbarCover)
            Glide.with(this)
                    .applyDefaultRequestOptions(RequestOptions()
                            .placeholder(R.drawable.ic_picture)
                            .error(R.drawable.ic_picture))
                    .load(modelinfo!![0].cover).transform(BlurTransformation(10, 3))
                    .transition(DrawableTransitionOptions()
                            .crossFade()).apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600)).into(object : CustomTarget<Drawable?>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                            binding.packageParent.background = resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            binding.packageParent.background = placeholder
                        }
                    })
            binding.toolbarCover.setOnClickListener {
                val fm = supportFragmentManager
                val alertDialog = CoverView()
                alertDialog.setUrl(modelinfo!![0].cover)
                alertDialog.show(fm, "coverview")
            }
            binding.packageRate.text = modelinfo!![0].rate
            val animeID = modelinfo!![0].pack + " / " + modelinfo!![0].mal
            binding.packageAnimeid.text = animeID
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
            binding.packageviewRecentContainer.visibility = View.GONE
        }
    }

    private fun parsePlayList(playlist: JSONArray) {
        try {
            playlistModels = ArrayList()
            val layoutManager = AutoGridLayoutManager(this, 500)
            binding.packageviewList.layoutManager = layoutManager
            for (i in 0 until playlist.length()) {
                val `object` = playlist.getJSONObject(i)
                val urlImg = `object`.getString("thumbnail")
                val title = `object`.getString("name_catalogue")
                val episode = `object`.getString("episode_anim")
                val idAnim = `object`.getString("id_anim")
                val pkg = `object`.getString("package_anim")
                playlistModels!!.add(PlaylistModel(urlImg, title, episode, idAnim, pkg))
            }
            adapter = PackageListAdapter(playlistModels, this, R.layout.rv_anim_packageview_selector)
            binding.packageviewList.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun readStarStatus() {
        withContext(Dispatchers.IO) {
            val isStarred = packageStarDBHelper!!.isStarred(pkganim!!)
            withContext(Dispatchers.Main) {
                if (isStarred) {
                    packStar!!.setIcon(R.drawable.ic_star)
                } else {
                    packStar!!.setIcon(R.drawable.ic_star_nofill)
                }
            }
        }
    }

    private suspend fun changeStar(change: String) {
        withContext(Dispatchers.IO) {
            if (change == "UNSTAR") {
                packageStarDBHelper!!.unStar(pkganim!!)
            } else if (change == "STAR") {
                packageStarDBHelper!!.addStar(pkganim!!)
            }
            val isStarred = packageStarDBHelper!!.isStarred(pkganim!!)
            withContext(Dispatchers.Main) {
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

    private suspend fun readRecent() {
        withContext(Dispatchers.IO) {
            val recentLand = recentPlayDBHelper!!.readRecentOnPackage(pkganim!!)
            withContext(Dispatchers.Main) {
                binding.packageviewRecentContainer.visibility = View.VISIBLE
                binding.packageviewRecentTitle.text = recentLand!!.packageName
                val ep = "Episode: " + recentLand.episode
                binding.packageviewRecentEpisode.text = ep
                Glide.with(baseContext)
                        .applyDefaultRequestOptions(RequestOptions()
                                .placeholder(R.drawable.ic_picture)
                                .error(R.drawable.ic_picture))
                        .load(recentLand.urlCover).transform(RoundedCornersTransformation(10, 0))
                        .transition(DrawableTransitionOptions()
                                .crossFade()).apply(RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600)).into(binding.packageviewRecentImg)
                binding.packageviewRecentContainer.setOnClickListener {
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