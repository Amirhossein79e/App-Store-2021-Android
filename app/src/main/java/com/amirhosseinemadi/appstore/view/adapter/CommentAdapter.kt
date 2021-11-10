package com.amirhosseinemadi.appstore.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.model.entity.CommentModel
import com.amirhosseinemadi.appstore.view.callback.Callback

class CommentAdapter(private val context:Context, private val list: List<CommentModel>, private val callback:Callback) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val commentModel:CommentModel = list.get(position)

        holder.txtName.text = commentModel.username
        holder.txtComment.text = commentModel.detail
        holder.ratingBar.rating = commentModel.rate!!

        if (commentModel.isAccess == 1)
        {
            holder.txtDelete.visibility = View.VISIBLE
            holder.txtDelete.setOnClickListener { callback.notify(commentModel) }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

        val txtName:AppCompatTextView = itemView.findViewById(R.id.txt_name)
        val ratingBar:AppCompatRatingBar = itemView.findViewById(R.id.rating_bar)
        val txtComment:AppCompatTextView = itemView.findViewById(R.id.txt_comment)
        val txtDelete:AppCompatTextView = itemView.findViewById(R.id.txt_delete)
    }
}
