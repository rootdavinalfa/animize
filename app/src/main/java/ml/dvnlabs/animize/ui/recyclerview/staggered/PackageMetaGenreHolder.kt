/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.recyclerview.staggered

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.ui.fragment.tabs.MultiView
import ml.dvnlabs.animize.ui.fragment.tabs.MultiView.Companion.newInstance

class PackageMetaGenreHolder(private val context: Context, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private val texts: TextView
    private val cards: CardView
    private var genre: String? = null
    fun bind_data(genre: String?) {
        texts.text = genre
        this.genre = genre
    }

    override fun onClick(v: View) {
        val activity = context as AppCompatActivity
        val multi: MultiView
        val bundle = Bundle()
        multi = newInstance()
        bundle.putString("genre", genre)
        multi.arguments = bundle
        multi.show(activity.supportFragmentManager, "genreFragment")
        //activity.getSupportFragmentManager().beginTransaction().add(multi,"genre").commit();
    }

    init {
        texts = view.findViewById(R.id.rv_staggered_text)
        cards = view.findViewById(R.id.rv_staggered_card)
        cards.setOnClickListener(this)
    }
}