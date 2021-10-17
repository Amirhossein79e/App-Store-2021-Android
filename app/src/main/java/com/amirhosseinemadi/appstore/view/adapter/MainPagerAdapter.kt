package com.amirhosseinemadi.appstore.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.squareup.picasso.Picasso

class MainPagerAdapter(private val context:Context,private val list:List<String>) : RecyclerView.Adapter<MainPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.pager_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(ApiCaller.SLIDER_URL+list.get(position)+".png").into(holder.img)
    }


    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.animate().translationY(1.1f).setDuration(300).start()
    }


    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView)
    {
        val img:AppCompatImageView = itemView.findViewById(R.id.img)
    }

}
