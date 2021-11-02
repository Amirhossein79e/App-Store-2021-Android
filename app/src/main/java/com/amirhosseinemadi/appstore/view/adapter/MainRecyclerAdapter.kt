package com.amirhosseinemadi.appstore.view.adapter

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.model.entity.HomeCategoryModel
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.view.callback.Callback

class MainRecyclerAdapter(private val context:Context, private val list: List<HomeCategoryModel>, private val callback:Callback) : RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {

    private val lang:String

    init
    {
        lang = PrefManager.getLang()!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.main_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var metrics:DisplayMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            context.display?.getRealMetrics(metrics)
        }else
        {
            metrics = context.resources.displayMetrics
        }

        val cardParams:ConstraintLayout.LayoutParams = holder.cardBack.layoutParams as ConstraintLayout.LayoutParams

        if(position % 2 == 0)
        {
            if (lang.equals("en"))
            {
                holder.cardBack.setBackgroundResource(R.drawable.main_item_card_background)
            }else
            {
                holder.cardBack.setBackgroundResource(R.drawable.main_item_card_background_alt)
            }
            (holder.recycler.layoutParams as FrameLayout.LayoutParams).marginStart = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4f,metrics).toInt()
            (holder.txt.layoutParams as ConstraintLayout.LayoutParams).startToEnd = R.id.guide_left_txt
            cardParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,0f,metrics).toInt()
            cardParams.startToEnd = R.id.guide_left
            cardParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }else
        {
            if (lang.equals("en"))
            {
                holder.cardBack.setBackgroundResource(R.drawable.main_item_card_background_alt)
            }else
            {
                holder.cardBack.setBackgroundResource(R.drawable.main_item_card_background)
            }
            (holder.recycler.layoutParams as FrameLayout.LayoutParams).marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4f,metrics).toInt()
            (holder.txt.layoutParams as ConstraintLayout.LayoutParams).startToEnd = R.id.guide_left_txt
            cardParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,0f,metrics).toInt()
            cardParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            cardParams.endToStart = R.id.guide_right
        }


        val homeCategoryModel:HomeCategoryModel = list.get(position)
        if (lang.equals("fa"))
        {
            holder.txt.text = homeCategoryModel.categoryName
        }else
        {
            holder.txt.text = homeCategoryModel.categoryNameEn
        }

        callback.notify(homeCategoryModel.category,holder.recycler,position)

    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView)
    {
        val cardBack:CardView = itemView.findViewById(R.id.card_back)
        val txt:AppCompatTextView = itemView.findViewById(R.id.txt)
        val recycler:RecyclerView = itemView.findViewById(R.id.recycler)
    }
}