package com.amirhosseinemadi.appstore.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.squareup.picasso.Picasso

class AppRecyclerAdapter(private val context:Context, private val list:List<AppModel>, private val callback:Callback) : RecyclerView.Adapter<AppRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.app_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val appModel:AppModel = list.get(position)
        holder.linear.setOnClickListener { callback.notify(appModel.packageName) }
        Picasso.get().load(ApiCaller.ICON_URL+appModel.packageName+".png").into(holder.img)

        if (PrefManager.getLang().equals("fa"))
        {
            holder.txt.text = appModel.nameFa
            holder.txtDev.text = context.getString(R.string.by)+" "+appModel.devFa
        }else
        {
            holder.txt.text = appModel.nameEn
            holder.txtDev.text = context.getString(R.string.by)+" "+appModel.devEn
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){

        val card:CardView = itemView.findViewById(R.id.card)
        val linear:ConstraintLayout = itemView.findViewById(R.id.linear)
        val img:AppCompatImageView = itemView.findViewById(R.id.img)
        val txt:AppCompatTextView = itemView.findViewById(R.id.txt)
        val txtDev:AppCompatTextView = itemView.findViewById(R.id.txt_dev)

    }
}