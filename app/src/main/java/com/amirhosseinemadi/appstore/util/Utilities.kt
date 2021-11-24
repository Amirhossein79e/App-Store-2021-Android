package com.amirhosseinemadi.appstore.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.customview.CustomSnack
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.google.android.material.snackbar.BaseTransientBottomBar
import org.json.JSONObject
import java.util.regex.Pattern

class Utilities {

    companion object
    {
        fun getUid() : String
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


        fun validateEmail(email:String) : Boolean
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


        fun underApiStatusBarHandle(activity: AppCompatActivity)
        {
            when(PrefManager.getTheme())
            {
                "light" -> WindowInsetsControllerCompat(activity.window,activity.window.decorView).isAppearanceLightStatusBars = true

                "dark" -> WindowInsetsControllerCompat(activity.window,activity.window.decorView).isAppearanceLightStatusBars = false
            }
        }


        fun dpToPx(context:Context, dp:Float) : Int
        {
            var metrics:DisplayMetrics = DisplayMetrics()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            {
                context.display?.getRealMetrics(metrics)
            }else
            {
                metrics = context.resources.displayMetrics
            }

            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,metrics).toInt()
        }


        fun onBackPressed(view:View, callback:Callback)
        {
            view.isFocusableInTouchMode = true
            view.requestFocus()
            view.setOnKeyListener { v, keyCode, event ->

                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP)
                {
                    callback.notify()
                }

                false
            }
        }


        fun loadingDialog(context: Context) : Dialog
        {
            val view:View = LayoutInflater.from(context).inflate(R.layout.dialog_loading,null)
            val dialog:Dialog = Dialog(context)
            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(AppCompatResources.getDrawable(context,R.drawable.dialog_background))
            dialog.setCancelable(false)
            dialog.setContentView(view)

            return dialog
        }


        fun dialogIcon(context:Context, @DrawableRes imgRes:Int?, @StringRes txtRes:Int, @StringRes posRes:Int?, @StringRes negRes:Int?, posVisibility:Boolean, negVisibility:Boolean, posListener:View.OnClickListener?, negListener:View.OnClickListener?) : Dialog
        {
            val view:View = LayoutInflater.from(context).inflate(R.layout.dialog,null)
            val dialog:Dialog = Dialog(context)
            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.window?.setBackgroundDrawable(AppCompatResources.getDrawable(context,R.drawable.dialog_background))

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


        fun showSnack(viewGroup:ViewGroup, text:String, @BaseTransientBottomBar.Duration duration:Int)
        {
            val customSnack:CustomSnack = CustomSnack.make(viewGroup,duration)
            customSnack.setText(text)
            customSnack.view.setBackgroundColor(ContextCompat.getColor(viewGroup.context,R.color.transparent))
            customSnack.show()
        }


        fun checkPackageInstalled(packageName:String) : Boolean
        {
            try
            {
                Application.component.context().packageManager.getPackageInfo(packageName,PackageManager.GET_ACTIVITIES)
                return true
            }catch (exception:PackageManager.NameNotFoundException)
            {
                return false
            }
        }


        fun getAllPackages() : List<JSONObject>
        {
            val list:List<PackageInfo> = Application.component.context().packageManager.getInstalledPackages(0)
            val appList:MutableList<JSONObject> = ArrayList()
            for (pInfo:PackageInfo in list)
            {
                val obj:JSONObject = JSONObject()
                obj.put("packageName",pInfo.packageName)
                obj.put("verName",pInfo.versionName)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                {
                    obj.put("verCode", pInfo.longVersionCode)
                }else
                {
                    obj.put("verCode", pInfo.versionCode)
                }
                appList.add(obj)
            }
            return appList
        }

    }

}