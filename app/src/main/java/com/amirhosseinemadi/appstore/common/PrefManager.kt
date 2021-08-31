package com.amirhosseinemadi.appstore.common

import android.content.Context
import android.content.SharedPreferences

class PrefManager() {

    companion object
    {
        public fun setToken(token: String)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("token", SecurityManager.storeDataEncrypt(token,"token"))
            editor.commit()
        }



        public fun getToken(): String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            var token: String? = preferences.getString("token", null)
            if (token != null)
            {
                token = SecurityManager.storeDataDecrypt(token,"token")
            }
            return token
        }



        public fun setAccess(access: String)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("access", SecurityManager.storeDataEncrypt(access,"access"))
            editor.commit()
        }



        public fun getAccess(): String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            var access: String? = preferences.getString("access", null)
            if (access != null)
            {
                access = SecurityManager.storeDataDecrypt(access,"access")
            }
            return access
        }



        public fun setUser(user: String)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("user", SecurityManager.storeDataEncrypt(user,"user"))
            editor.commit()
        }



        public fun getUser(): String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            var user: String? = preferences.getString("user", null)
            if (user != null)
            {
                user = SecurityManager.storeDataDecrypt(user,"user")
            }
            return user
        }
    }


}