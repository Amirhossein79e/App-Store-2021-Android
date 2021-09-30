package com.amirhosseinemadi.appstore.util

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.common.Application

class Utilities {

    companion object
    {
        public fun dialogIcon(@DrawableRes imgRes:Int?, @StringRes txtRes:Int, @StringRes posRes:Int?, @StringRes negRes:Int?, posVisibility:Boolean, negVisibility:Boolean, posListener:View.OnClickListener?, negListener:View.OnClickListener?) : Dialog
        {
            val view:View = LayoutInflater.from(Application.component.context()).inflate(R.layout.icon_dialog,null)
            val dialog:Dialog = Dialog(Application.component.context())
            dialog.setContentView(view)

            if (imgRes != null)
            {
                view.findViewById<AppCompatImageView>(R.id.img).let{
                    it.visibility = View.VISIBLE
                    it.setImageResource(imgRes)
                }
            }

            view.findViewById<AppCompatTextView>(R.id.txt).text = Application.component.context().getText(txtRes)

            if (posVisibility)
            {
                view.findViewById<AppCompatButton>(R.id.btn_pos).let {
                    it.visibility = View.VISIBLE
                    if (posRes != null)
                    {
                        it.text = Application.component.context().getText(posRes)
                    }
                    it.setOnClickListener(posListener)
                }
            }

            if (negVisibility)
            {
                view.findViewById<AppCompatButton>(R.id.btn_neg).let {
                    it.visibility = View.VISIBLE
                    if (negRes != null)
                    {
                        it.text = Application.component.context().getText(negRes)
                    }
                    if (negListener != null)
                    {
                        it.setOnClickListener(negListener)
                    }else
                    {
                        it.setOnClickListener { dialog.dismiss() }
                    }

                }
            }

            return dialog
        }
    }

}