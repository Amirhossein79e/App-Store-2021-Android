package com.amirhosseinemadi.appstore.customview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.amirhosseinemadi.appstore.R
import com.google.android.material.snackbar.BaseTransientBottomBar

class CustomSnack(viewGroup:ViewGroup, view: View, contentViewCallback:com.google.android.material.snackbar.ContentViewCallback) : BaseTransientBottomBar<CustomSnack>(viewGroup,view,contentViewCallback) {


    companion object
    {
        public fun make(viewGroup:ViewGroup, @Duration duration: Int) : CustomSnack
        {
            val inflater:LayoutInflater = LayoutInflater.from(viewGroup.context)
            val view:View = inflater.inflate(R.layout.snack,viewGroup,false)
            val contentViewCallback:MyContentViewCallback = MyContentViewCallback(view)
            val customSnack:CustomSnack = CustomSnack(viewGroup,view,contentViewCallback)
            customSnack.getView().setPadding(0,0,0,0)
            customSnack.duration = duration
            return customSnack
        }
    }


    public fun setText(text:String) : CustomSnack
    {
        getView().findViewById<AppCompatTextView>(R.id.txt).text = text
        return this
    }


    private class MyContentViewCallback(val view:View) : com.google.android.material.snackbar.ContentViewCallback {
        override fun animateContentIn(delay: Int, duration: Int) {
            //view.scaleY = 0f
            view.animate()
                .scaleY(1f)
                .setDuration(duration.toLong())
                .setStartDelay(delay.toLong())
                .start()
        }

        override fun animateContentOut(delay: Int, duration: Int) {
            view.scaleY = 1f
            view.animate()
                .scaleY(0f)
                .setDuration(duration.toLong())
                .setStartDelay(delay.toLong())
                .start()
        }

    }

}