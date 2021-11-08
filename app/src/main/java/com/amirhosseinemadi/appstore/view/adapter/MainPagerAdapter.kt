package com.amirhosseinemadi.appstore.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.squareup.picasso.Picasso

class MainPagerAdapter(private val context:Context,private val list:List<String>,private val callback:Callback) : RecyclerView.Adapter<MainPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.pager_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val str:String = list.get(position)
        Picasso.get().load(ApiCaller.SLIDER_URL+list.get(position)+".png").into(holder.img)
        holder.img.setOnClickListener { callback.notify(str) }

    }


    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView)
    {
        val img:AppCompatImageView = itemView.findViewById(R.id.img)
    }

}
