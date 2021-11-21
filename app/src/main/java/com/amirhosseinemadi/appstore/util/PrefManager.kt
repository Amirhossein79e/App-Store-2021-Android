package com.amirhosseinemadi.appstore.util

import android.content.Context
import android.content.SharedPreferences
import com.amirhosseinemadi.appstore.common.Application

class PrefManager() {

    companion object
    {
        fun setFirst(isFirst:Boolean)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor:SharedPreferences.Editor = preferences.edit()
            editor.putBoolean("first",isFirst)
            editor.commit()
        }


        fun getFirst() : Boolean
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val isFirst:Boolean = preferences.getBoolean("first",true)
            return isFirst
        }


        fun setLang(language:String)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor:SharedPreferences.Editor = preferences.edit()
            editor.putString("language",language)
            editor.commit()
        }


        fun getLang() : String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val lang:String? = preferences.getString("language","en")
            return lang
        }


        fun setTheme(mode:String)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor:SharedPreferences.Editor = preferences.edit()
            editor.putString("theme",mode)
            editor.commit()
        }


        fun getTheme() : String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val mode:String? = preferences.getString("theme","dark")
            return mode
        }


        fun setUpdate(update:Boolean)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor:SharedPreferences.Editor = preferences.edit()
            editor.putBoolean("update",update)
            editor.commit()
        }


        fun getUpdate() : Boolean
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val update:Boolean = preferences.getBoolean("update",true)
            return update
        }


        fun setToken(token: String)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("token", SecurityManager.keyStoreEncrypt(token,"token"))
            editor.commit()
        }


        fun getToken() : String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            var token: String? = preferences.getString("token", null)
            if (token != null)
            {
                token = SecurityManager.keyStoreDecrypt(token,"token")
            }
            return token
        }


        fun setAccess(access: String?)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            if (access != null)
            {
                editor.putString("access", SecurityManager.keyStoreEncrypt(access, "access"))
            }else
            {
                editor.putString("access",access)
            }
            editor.commit()
        }


        fun getAccess() : String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            var access: String? = preferences.getString("access", null)
            if (access != null)
            {
                access = SecurityManager.keyStoreDecrypt(access,"access")
            }
            return access
        }


        fun setUser(user: String?)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            if (user != null)
            {
                editor.putString("user", SecurityManager.keyStoreEncrypt(user, "user"))
            }else
            {
                editor.putString("user",user)
            }
            editor.commit()
        }


        fun getUser() : String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            var user: String? = preferences.getString("user", null)
            if (user != null)
            {
                user = SecurityManager.keyStoreDecrypt(user,"user")
            }
            return user
        }


        fun checkSignIn() : Boolean
        {
            return getUser() != null && getAccess() != null
        }

    }


}