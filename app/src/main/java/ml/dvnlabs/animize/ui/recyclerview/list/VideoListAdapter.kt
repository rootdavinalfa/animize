/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */
package ml.dvnlabs.animize.ui.recyclerview.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import ml.dvnlabs.animize.R
import ml.dvnlabs.animize.model.VideoListModel
import ml.dvnlabs.animize.ui.recyclerview.interfaces.OnLoadMoreListener
import java.util.*

class VideoListAdapter(private val video_data: ArrayList<VideoListModel>?, private val mcontext: Context, //private int itemResor;
                       private val itemResor: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listener: OnLoadMoreListener? = null
    private var isLoading = false
    private val visibleThreshold = 10
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    override fun getItemViewType(position: Int): Int {
        val viewtype: Int
        viewtype = if (video_data!![position].idn == null) {
            //log.e("LOADINGggg:","OK");
            VIEW_TYPE_LOADING
        } else {
            //log.e("LOADINGggg:","OK");
            VIEW_TYPE_ITEM
        }
        return viewtype
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            //log.e("DATA:","OK");
            val view = LayoutInflater.from(parent.context).inflate(R.layout.video_list_view, parent, false)
            VideoListHolder(mcontext, view)
        } else{
            //log.e("LOADING:","OK");
            val view = LayoutInflater.from(parent.context).inflate(R.layout.video_list_view_load, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                //log.e("DATSA:","OK");
                val slm = video_data!![position]
                val hold = holder as VideoListHolder
                hold.bindvideo_list(slm)
            }
            1 -> {
                //log.e("LOADINGS:","OK");
                val loadingViewHolder = holder as LoadingViewHolder
                loadingViewHolder.frame.startShimmer()
            }
        }
    }

    fun setOnloadMoreListener(listen: OnLoadMoreListener?) {
        listener = listen
    }

    fun setLoaded() {
        isLoading = false
    }

    override fun getItemCount(): Int {
        return video_data?.size ?: 0
    }

    private inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val frame: ShimmerFrameLayout

        init {
            frame = view.findViewById<View>(R.id.shimmer_lastup) as ShimmerFrameLayout
        }
    } /*
    @Override
    public video_list_holder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResor,parent,false);
        return new video_list_holder(this.mcontext,view);

    }

    @Override
    public void onBindViewHolder(video_list_holder holder,int position){
        video_list_model vlm = this.video_data.get(position);
        holder.bindvideo_list(vlm);

    }
    @Override
    public int getItemCount(){
        ////log.e("SIZE:",String.valueOf(video_data.size()));
        if(video_data == null){
            return 0;
        }else{
            return video_data.size();
        }

    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public int getItemViewType(int position){
        return position;
    }*/

    init {
        //super(context, R.layout.video_list_view,data);
        val linearLayoutManager = itemResor.layoutManager as LinearLayoutManager?
        itemResor.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager!!.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (listener != null) {
                        listener!!.onLoadMore()
                    }
                    isLoading = true
                }
            }
        })
    }
}