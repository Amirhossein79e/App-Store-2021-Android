package com.amirhosseinemadi.appstore.util

import android.content.Context
import android.content.SharedPreferences
import com.amirhosseinemadi.appstore.common.Application

class PrefManager() {

    companion object
    {
        public fun setFirst(isFirst:Boolean)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor:SharedPreferences.Editor = preferences.edit()
            editor.putBoolean("first",isFirst)
            editor.commit()
        }


        public fun getFirst() : Boolean
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val isFirst:Boolean = preferences.getBoolean("first",true)
            return isFirst
        }


        public fun setLang(language:String)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor:SharedPreferences.Editor = preferences.edit()
            editor.putString("language",language)
            editor.commit()
        }


        public fun getLang() : String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val lang:String? = preferences.getString("language","en")
            return lang
        }


        public fun setToken(token: String)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("token", SecurityManager.storeDataEncrypt(token,"token"))
            editor.commit()
        }


        public fun getToken() : String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            var token: String? = preferences.getString("token", null)
            if (token != null)
            {
                token = SecurityManager.storeDataDecrypt(token,"token")
            }
            return token
        }


        public fun setAccess(access: String?)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            if (access != null)
            {
                editor.putString("access", SecurityManager.storeDataEncrypt(access, "access"))
            }else
            {
                editor.putString("access",access)
            }
            editor.commit()
        }


        public fun getAccess() : String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            var access: String? = preferences.getString("access", null)
            if (access != null)
            {
                access = SecurityManager.storeDataDecrypt(access,"access")
            }
            return access
        }


        public fun setUser(user: String?)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            if (user != null)
            {
                editor.putString("user", SecurityManager.storeDataEncrypt(user, "user"))
            }else
            {
                editor.putString("user",user)
            }
            editor.commit()
        }


        public fun getUser() : String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            var user: String? = preferences.getString("user", null)
            if (user != null)
            {
                user = SecurityManager.storeDataDecrypt(user,"user")
            }
            return user
        }


        public fun checkSignIn() : Boolean
        {
            return getUser() != null && getAccess() != null
        }

    }


}