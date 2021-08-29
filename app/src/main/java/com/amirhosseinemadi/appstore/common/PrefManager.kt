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
            editor.putString("token", token)
            editor.commit()
        }


        public fun getToken(): String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val token: String? = preferences.getString("token", null)
            return token
        }


        public fun setAccess(access: String)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("access", access)
            editor.commit()
        }


        public fun getAccess(): String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val access: String? = preferences.getString("access", null)
            return access
        }


        public fun setUser(user: String)
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("user", user)
            editor.commit()
        }


        public fun getUser(): String?
        {
            val preferences: SharedPreferences = Application.component.context().getSharedPreferences("main", Context.MODE_PRIVATE)
            val user: String? = preferences.getString("user", null)
            return user
        }
    }


}