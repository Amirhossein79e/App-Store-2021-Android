package com.amirhosseinemadi.appstore.view.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R

class CommentAdapter() {



    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

        val txtName:AppCompatTextView = itemView.findViewById(R.id.txt_name)
        val txtRate:AppCompatTextView = itemView.findViewById(R.id.txt_name)
        val txtComment:AppCompatTextView = itemView.findViewById(R.id.txt_name)
    }
}