package com.amirhosseinemadi.appstore.view.adapter

import android.content.Context
import android.util.TypedValue
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
import com.amirhosseinemadi.appstore.model.entity.CategoryModel
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.squareup.picasso.Picasso

class CategoryRecyclerAdapter(private val context:Context, private val list:List<CategoryModel>, private val callback:Callback) : RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.category_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val categoryModel:CategoryModel = list.get(position)
        holder.layout.setOnClickListener { callback.notify(categoryModel.categoryEn) }
        Picasso.get().load(ApiCaller.CATEGORY_URL+categoryModel.icon).into(holder.img)

        if (PrefManager.getLang().equals("fa"))
        {
            holder.txt.text = categoryModel.categoryFa
        }else
        {
            holder.txt.text = categoryModel.categoryEn
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

        val card:CardView = itemView.findViewById(R.id.card)
        val img:AppCompatImageView = itemView.findViewById(R.id.img)
        val txt:AppCompatTextView = itemView.findViewById(R.id.txt)
        val layout:ConstraintLayout = itemView.findViewById(R.id.linear)

    }
}