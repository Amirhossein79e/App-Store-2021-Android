package com.amirhosseinemadi.appstore.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.view.callback.Callback

class TitleRecyclerAdapter(private val context:Context,private val list:List<String>,private val callback:Callback) : RecyclerView.Adapter<TitleRecyclerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.title_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val str:String = list.get(position)
        holder.txt.text = str
        holder.linear.setOnClickListener { callback.notify(str) }

        if (position == list.size - 1)
        {
            holder.txtDivider.visibility = View.GONE
        }else
        {
            holder.txtDivider.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

        val txt:AppCompatTextView = itemView.findViewById(R.id.txt)
        val txtDivider:AppCompatTextView = itemView.findViewById(R.id.txt_divider)
        val linear:ConstraintLayout = itemView.findViewById(R.id.linear)

    }
}