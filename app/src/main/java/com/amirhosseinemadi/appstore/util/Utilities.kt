package com.amirhosseinemadi.appstore.util

import android.app.Dialog
import android.content.Context
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.WindowInsetsControllerCompat
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.customview.CustomSnack
import com.google.android.material.snackbar.BaseTransientBottomBar
import java.time.Duration
import java.util.regex.Pattern

class Utilities {

    companion object
    {
        public fun getUid() : String
        {
            val uid: String = Settings.Secure.getString(Application.component.context().contentResolver, Settings.Secure.ANDROID_ID)
            var refactoredUid = ""

            if (uid.length == 16)
            {
                for (i: Int in uid.indices)
                {
                    if (i % 2 == 0 && i > 1)
                    {
                        refactoredUid += ":"
                    }
                    refactoredUid += uid.get(i)
                }
            }else if (uid.length == 23)
            {
                refactoredUid = uid
            }

            return refactoredUid
        }


        public fun underApiStatusBarHandle(activity: AppCompatActivity)
        {
            when(activity.delegate.localNightMode)
            {
                AppCompatDelegate.MODE_NIGHT_NO -> WindowInsetsControllerCompat(activity.window,activity.window.decorView).isAppearanceLightStatusBars = true

                else -> WindowInsetsControllerCompat(activity.window,activity.window.decorView).isAppearanceLightStatusBars = false
            }
        }


        public fun loadingDialog(context: Context) : Dialog
        {
            val view:View = LayoutInflater.from(context).inflate(R.layout.dialog_loading,null)
            val dialog:Dialog = Dialog(context)
            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.window?.setBackgroundDrawable(context.getDrawable(R.drawable.dialog_background))
            return dialog
        }


        public fun dialogIcon(context:Context, @DrawableRes imgRes:Int?, @StringRes txtRes:Int, @StringRes posRes:Int?, @StringRes negRes:Int?, posVisibility:Boolean, negVisibility:Boolean, posListener:View.OnClickListener?, negListener:View.OnClickListener?) : Dialog
        {
            val view:View = LayoutInflater.from(context).inflate(R.layout.dialog,null)
            val dialog:Dialog = Dialog(context)
            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.window?.setBackgroundDrawable(context.getDrawable(R.drawable.dialog_background))

            if (imgRes != null)
            {
                view.findViewById<AppCompatImageView>(R.id.img).let{
                    it.visibility = View.VISIBLE
                    it.setImageResource(imgRes)
                }
            }

            view.findViewById<AppCompatTextView>(R.id.txt).text = context.getText(txtRes)

            if (posVisibility)
            {
                view.findViewById<AppCompatButton>(R.id.btn_pos).let {
                    it.visibility = View.VISIBLE
                    if (posRes != null)
                    {
                        it.text = context.getText(posRes)
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
                        it.text = context.getText(negRes)
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


        public fun showSnack(viewGroup:ViewGroup, text:String, @BaseTransientBottomBar.Duration duration:Int)
        {
            CustomSnack.make(viewGroup,duration).setText(text).show()
        }


        public fun validateEmail(email:String) : Boolean
        {
            val pattern:Pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",Pattern.CASE_INSENSITIVE)
            return pattern.matcher(email).find()
        }


        public fun validatePassword(password:String) : Boolean
        {
            var valid = false
            if (password.length > 7)
            {
                valid = true
            }
            return valid
        }

    }

}