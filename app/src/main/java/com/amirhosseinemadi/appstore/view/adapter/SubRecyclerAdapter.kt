package com.amirhosseinemadi.appstore.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.squareup.picasso.Picasso

class SubRecyclerAdapter(private val context:Context, private  val list: List<AppModel>, private val callback:Callback) : RecyclerView.Adapter<SubRecyclerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.sub_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val appModel:AppModel = list.get(position)
        holder.linear.setOnClickListener { callback.notify(appModel.packageName) }

        Picasso.get().load(ApiCaller.ICON_URL+appModel.packageName+".png").into(holder.img)

        if (PrefManager.getLang().equals("fa"))
        {
            holder.txt.text = appModel.nameFa
        }else
        {
            holder.txt.text = appModel.nameEn
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView)
    {
        val linear:LinearLayout = itemView.findViewById(R.id.linear)
        val img:AppCompatImageView = itemView.findViewById(R.id.img)
        val txt:AppCompatTextView = itemView.findViewById(R.id.txt)
    }
}