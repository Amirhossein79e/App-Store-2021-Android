package com.amirhosseinemadi.appstore.view.adapter

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.squareup.picasso.Picasso

class AppImageAdapter(private val context:Context, private val packageName:String, private val imageNum:Int) : RecyclerView.Adapter<AppImageAdapter.ViewHolder>() {

    private val list:MutableList<String>

    init
    {
        list = ArrayList()
        for (i:Int in 1 until imageNum+1)
        {
            list.add(i.toString())
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.app_image_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val str:String = list.get(position)

        if (position != list.size-1)
        {
            var metrics:DisplayMetrics = DisplayMetrics()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            {
                context.display?.getRealMetrics(metrics)
            }else
            {
                metrics = context.resources.displayMetrics
            }
            (holder.itemView.layoutParams as RecyclerView.LayoutParams).marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, metrics).toInt()
        }

        Picasso.get().load(ApiCaller.IMAGE_URL+packageName+".$str.jpg").into(holder.img)

    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){

        val img:AppCompatImageView = itemView.findViewById(R.id.img)

    }
}