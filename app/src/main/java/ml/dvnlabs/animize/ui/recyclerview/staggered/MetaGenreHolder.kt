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
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.model.MetaGenreModel

class MetaGenreHolder(private val context: Context, view: View, cal: GotoPageGenre) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private var data: MetaGenreModel? = null
    private val texts: TextView
    private val cards: CardView
    private var pos = 0
    var adapter: MetaGenreAdapter? = null
    private val gotoPageGenre: GotoPageGenre
    fun bind_data(dat: MetaGenreModel?, poss: Int) {
        data = dat
        val genre_count = data!!.title + " (" + data!!.count + ")"
        texts.text = genre_count
        //this.pos = position;
        pos = poss
    }

    override fun onClick(v: View) {
        /*if (data!=null){
            gotopage_genre.gotopager(pos);
        }*/
        gotoPageGenre.gotoPager(pos)
    }

    interface GotoPageGenre {
        fun gotoPager(page: Int)
    }

    init {
        texts = view.findViewById(R.id.rv_staggered_text)
        cards = view.findViewById(R.id.rv_staggered_card)
        gotoPageGenre = cal
        cards.setOnClickListener(this)
    }
}