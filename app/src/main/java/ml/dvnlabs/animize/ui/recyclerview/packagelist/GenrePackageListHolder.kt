/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.recyclerview.packagelist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.kennyc.bottomsheet.BottomSheetListener
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.database.Anime
import ml.dvnlabs.animize.database.AnimizeDatabase
import ml.dvnlabs.animize.model.GenrePackageList
import ml.dvnlabs.animize.ui.activity.PackageView
import ml.dvnlabs.animize.ui.fragment.bottom.GenreSheet
import ml.dvnlabs.animize.ui.fragment.tabs.MultiView
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class GenrePackageListHolder(private val context: Context, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, OnLongClickListener, KoinComponent {
    private val episode: TextView = view.findViewById(R.id.genrepackage_episode)
    private val rate: TextView = view.findViewById(R.id.genrepackages_rate)
    private val mal: TextView = view.findViewById(R.id.genrepackage_mal)
    private val title: TextView = view.findViewById(R.id.genrepackage_name)
    private val thumbnail: ImageView = view.findViewById(R.id.genrepackage_cover)
    private val container: CardView = view.findViewById(R.id.genrepack_container)
    private var data: GenrePackageList? = null
    private val animizeDB: AnimizeDatabase by inject { parametersOf(context) }
    fun bindPlaylist(plm: GenrePackageList?) {
        data = plm
        title.text = data!!.getName()
        val ep_string = data!!.now + " " + context.getString(R.string.string_of) + " " + data!!.tot
        episode.text = ep_string
        rate.text = data!!.rate
        val mals = "MAL: " + data!!.mal
        mal.text = mals
        Glide.with(context)
                .applyDefaultRequestOptions(RequestOptions()
                        .placeholder(R.drawable.ic_picture_light)
                        .error(R.drawable.ic_picture_light))
                .load(data!!.cover)
                .transition(DrawableTransitionOptions()
                        .crossFade()).apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).override(424, 600)).into(thumbnail)
        val multi = MultiTransformation(
                BlurTransformation(20), RoundedCornersTransformation(5, 0)
        )
        Glide.with(context)
                .applyDefaultRequestOptions(RequestOptions()
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture))
                .load(data!!.cover).transform(multi)
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
        if (data != null) {
            val activity = context as AppCompatActivity
            if (activity is PackageView) {
                val fragment = activity.getSupportFragmentManager().findFragmentByTag("genreFragment")
                when (fragment) {
                    is MultiView -> {
                        fragment.dismiss()
                    }
                    is GenreSheet -> {
                        fragment.dismiss()
                    }
                }
                activity.refreshActivity(data!!.pack)
            } else {
                val intent = Intent(context, PackageView::class.java)
                intent.putExtra("package", data!!.pack)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                Log.e("CLICK:", data!!.pack)
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        if (data != null) {
            var isStarred: Boolean
            val activity = context as AppCompatActivity
            @SuppressLint("RestrictedApi") val menu: Menu = MenuBuilder(context)
            GlobalScope.launch {
                animizeDB.animeDAO().newAnime(Anime(
                        packageID = data!!.pack,
                        packageName = null,
                        episodeTotal = 0,
                        updatedOn = System.currentTimeMillis()
                ))
                if (animizeDB.animeDAO().isAnimeStarred(data!!.pack)) {
                    isStarred = true
                    menu.add(0, Menu.FIRST, 0, "UnStar This Package").setIcon(R.drawable.ic_star_nofill)
                } else {
                    isStarred = false
                    menu.add(0, Menu.FIRST, 0, "Star This Package").setIcon(R.drawable.ic_star)
                }
                BottomSheetMenuDialogFragment.Builder(context)
                        .dark()
                        .setMenu(menu)
                        .setTitle("What do you want ?")
                        .setListener(object : BottomSheetListener {
                            override fun onSheetShown(bottomSheet: BottomSheetMenuDialogFragment, `object`: Any?) {}
                            override fun onSheetItemSelected(bottomSheet: BottomSheetMenuDialogFragment, item: MenuItem, `object`: Any?) {
                                if (isStarred) {
                                    GlobalScope.launch {
                                        changeStar("UNSTAR")
                                    }
                                } else {
                                    GlobalScope.launch {
                                        changeStar("STAR")
                                    }
                                }
                            }

                            override fun onSheetDismissed(bottomSheet: BottomSheetMenuDialogFragment, `object`: Any?, dismissEvent: Int) {}
                        })
                        .show(activity.supportFragmentManager, "select_package")
            }
        }
        return true
    }

    private suspend fun changeStar(change: String) {
        withContext(Dispatchers.IO) {
            if (change == "UNSTAR") {
                animizeDB.animeDAO().changeStarred(data!!.pack, false)
            } else if (change == "STAR") {
                animizeDB.animeDAO().changeStarred(data!!.pack, true)
            }

            val status = if (animizeDB.animeDAO().isAnimeStarred(data!!.pack)) {
                "Add to Star Success"
            } else {
                "Remove Star Success"
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    init {
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }
}