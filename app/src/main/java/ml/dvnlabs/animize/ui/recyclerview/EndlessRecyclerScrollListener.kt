/*
 * Copyright (c) 2020. 
 * Animize Devs 
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *   
 */

package ml.dvnlabs.animize.ui.recyclerview

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

public abstract class EndlessRecyclerScrollListener(linearLayoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener(){
    private var previousTotal = 0 // The total number of items in the dataset after the last load
    private var loading = true // True if we are still waiting for the last set of data to load.
    private val visibleThreshold = 1 // The minimum amount of items to have below your current scroll position before loading more.
    internal var firstVisibleItem: Int = 0
    internal var visibleItemCount:Int = 0
    internal var totalItemCount:Int = 0

    private var current_page = 1

    private var mLinearLayoutManager: LinearLayoutManager = linearLayoutManager


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = recyclerView.childCount
        totalItemCount = mLinearLayoutManager.getItemCount()
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()

        if (loading) {
            if (totalItemCount > previousTotal + 1) {
                loading = false
                previousTotal = totalItemCount
            }
        }
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            // End has been reached
            // Do something
            current_page++
            onLoadMore(current_page)
            loading = true
        }
    }

    abstract fun onLoadMore(current_page: Int)

}