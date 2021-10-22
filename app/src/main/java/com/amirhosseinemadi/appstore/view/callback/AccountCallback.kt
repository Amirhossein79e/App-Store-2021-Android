package com.amirhosseinemadi.appstore.view.callback

import androidx.annotation.StringRes

interface AccountCallback {

    public fun onShow()

    public fun onHide()

    public fun signUp(email:String, password:String, username:String,token:String)

    public fun signIn(email:String, password:String)

    public fun onMessage(message:String)

    public fun getStr(@StringRes res:Int) : String

}