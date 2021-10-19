package com.amirhosseinemadi.appstore.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.model.entity.HomeCategoryModel

class MainRecyclerAdapter(private val context:Context, private val list: List<HomeCategoryModel>) : RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.main_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val homeCategoryModel:HomeCategoryModel = list.get(position)
        holder.txt.text = homeCategoryModel.categoryName

    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView)
    {
        val txt:AppCompatTextView = itemView.findViewById(R.id.txt)
        val recycler:RecyclerView = itemView.findViewById(R.id.recycler)
    }
}